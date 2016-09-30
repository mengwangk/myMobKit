#include <stdio.h>
#include <string.h>
#include <jni.h>
#include <android/log.h>

#include "YuvToJpegEncoderMT.h"

static unsigned char *yuv;
static int SX = 0;
static int SY = 0;


extern "C" JNIEXPORT jboolean JNICALL Java_com_mymobkit_model_YuvImage_SaveJpegFreeOutMT
(
	JNIEnv* env, jobject, jbyteArray inYuv,
	int format, int width, int height, jintArray offsets,
	jintArray strides, int jpegQuality, jobject jstream, jbyteArray jstorage
)
{
	initStreamMethods(env);

	jint* imgOffsets = env->GetIntArrayElements(offsets, NULL);
	jint* imgStrides = env->GetIntArrayElements(strides, NULL);
    jbyte* yuv = env->GetByteArrayElements(inYuv, NULL);

	if (YuvToJpegEncoderMT_init(format, imgStrides))
	{
		return false;
	}

	boolean result = true;

	result = YuvToJpegEncoderMT_encode(env, jstream, jstorage, yuv, width, height, imgOffsets, imgStrides, jpegQuality, format);

    env->ReleaseByteArrayElements(inYuv, yuv, 0);
	env->ReleaseIntArrayElements(offsets, imgOffsets, 0);
	env->ReleaseIntArrayElements(strides, imgStrides, 0);
	return result;
}

extern "C" JNIEXPORT void JNICALL Java_com_mymobkit_model_YuvImage_RemoveFrame
(
	JNIEnv* env,
	jobject thiz
)
{
	free((void*)yuv);
}

/*
extern "C" JNIEXPORT jint JNICALL Java_com_mymobkit_model_YuvImage_GetFrame
(
	JNIEnv* env,
	jobject thiz
)
{
	return (jint)yuv;
}
*/
extern "C" JNIEXPORT jbyte* JNICALL Java_com_mymobkit_model_YuvImage_GetByteFrame
(
	JNIEnv* env,
	jobject thiz
)
{
	int x, y;
	jbyteArray jpixels = NULL;
	unsigned char * pixels;

	jpixels = env->NewByteArray(SX*SY+SX*((SY+1)/2));
	pixels = (unsigned char *)env->GetByteArrayElements(jpixels, NULL);
	memcpy (pixels, yuv, SX*SY+SX*((SY+1)/2));
	env->ReleaseByteArrayElements(jpixels, (jbyte*)pixels, 0);

	free(yuv);

	return (jbyte *)jpixels;
}


void ExtractYuvFromDirectBuffer
(
	unsigned char *Y,
	unsigned char *U,
	unsigned char *V,
	unsigned char *yuv,
	jint pixelStrideY,
	jint rowStrideY,
	jint pixelStrideU,
	jint rowStrideU,
	jint pixelStrideV,
	jint rowStrideV,
	jint sx,
	jint sy
)
{
	int i, y;

	// Note: assumption of:
	// - even w, h, x0 here
	// - pixelStrideY=1 (guaranteed by android doc)
	// - U,V being sub-sampled 2x horizontally and vertically
	#pragma omp parallel for schedule(guided)
	for (y=0; y<sy; y+=2)
	{
		int x;

		// Y
		memcpy (&yuv[y*sx],     &Y[y*rowStrideY],   sx);
		memcpy (&yuv[(y+1)*sx], &Y[(y+1)*rowStrideY], sx);

		// UV - no direct memcpy as swap may be needed
		for (x=0; x<sx/2; ++x)
		{
			// V
			yuv[sx*sy+(y/2)*sx+x*2]   = V[x*pixelStrideV + (y/2)*rowStrideV];

			// U
			yuv[sx*sy+(y/2)*sx+x*2+1] = U[x*pixelStrideU + (y/2)*rowStrideU];
		}
	}

}


extern "C" JNIEXPORT int JNICALL Java_com_mymobkit_model_YuvImage_CreateYUVImage
(
		JNIEnv* env,
		jobject thiz,
		jobject bufY,
		jobject bufU,
		jobject bufV,
		jint pixelStrideY,
		jint rowStrideY,
		jint pixelStrideU,
		jint rowStrideU,
		jint pixelStrideV,
		jint rowStrideV,
		jint sx,
		jint sy
)
{
	unsigned char *Y, *U, *V;

	// All Buffer objects have an effectiveDirectAddress field
	Y = (unsigned char*)env->GetDirectBufferAddress(bufY);
	U = (unsigned char*)env->GetDirectBufferAddress(bufU);
	V = (unsigned char*)env->GetDirectBufferAddress(bufV);

	if ((Y == NULL) || (U == NULL) || (V == NULL))
		return -1;

	SX = sx;
	SY = sy;

	// extract as NV21 image
	yuv = (unsigned char *)malloc (sx*sy+sx*((sy+1)/2));
	if (yuv == NULL)
		return -2;

	ExtractYuvFromDirectBuffer(Y, U, V, yuv, pixelStrideY, rowStrideY, pixelStrideU, rowStrideU, pixelStrideV, rowStrideV, sx, sy);

	//__android_log_print(ANDROID_LOG_INFO, "OpenCamera. CreateYUV", "NV21 created");

	return 0;
}


extern "C" JNIEXPORT jbyte* JNICALL Java_com_mymobkit_model_YuvImage_CreateYUVImageByteArray
(
		JNIEnv* env,
		jobject thiz,
		jobject bufY,
		jobject bufU,
		jobject bufV,
		jint pixelStrideY,
		jint rowStrideY,
		jint pixelStrideU,
		jint rowStrideU,
		jint pixelStrideV,
		jint rowStrideV,
		jint sx,
		jint sy
)
{
	unsigned char *Y, *U, *V;

	// All Buffer objects have an effectiveDirectAddress field
	Y = (unsigned char*)env->GetDirectBufferAddress(bufY);
	U = (unsigned char*)env->GetDirectBufferAddress(bufU);
	V = (unsigned char*)env->GetDirectBufferAddress(bufV);

	if ((Y == NULL) || (U == NULL) || (V == NULL))
		return NULL;

	SX = sx;
	SY = sy;

	// extract crop as NV21 image
	jbyteArray jpixels = NULL;
	unsigned char * single_yuv;

	jpixels = env->NewByteArray(SX*SY+SX*((SY+1)/2));

	single_yuv = (unsigned char *)env->GetByteArrayElements(jpixels, NULL);
	if (single_yuv == NULL)
		return NULL;

	ExtractYuvFromDirectBuffer(Y, U, V, single_yuv, pixelStrideY, rowStrideY, pixelStrideU, rowStrideU, pixelStrideV, rowStrideV, sx, sy);

	env->ReleaseByteArrayElements(jpixels, (jbyte*)single_yuv, 0);

	return (jbyte *)jpixels;
}

/*
extern "C" JNIEXPORT int JNICALL Java_com_mymobkit_model_YuvImage_AllocateMemoryForYUV
(
		JNIEnv* env,
		jobject thiz,
		jint sx,
		jint sy
)
{
	unsigned char* yuv_mem = (unsigned char *)malloc (sx*sy+sx*((sy+1)/2));
	return (jint)yuv_mem;
}
*/

