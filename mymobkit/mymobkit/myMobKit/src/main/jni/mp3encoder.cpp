#include "mp3encoder.h"

#include "libmp3lame/lame.h"

#define  JNIDEFINE(fname) Java_com_mymobkit_audio_codec_Mp3Encoder_##fname
extern "C" {
JNIEXPORT jint JNICALL JNIDEFINE(nativeOpenEncoder)(JNIEnv* env, jclass clz);
JNIEXPORT jint JNICALL JNIDEFINE(nativeEncodingPCM)(JNIEnv* env, jclass clz,
		jbyteArray pcmData, jint length, jbyteArray mp3Data);
JNIEXPORT jbyteArray JNICALL JNIDEFINE(nativeEncodingPCMv2)(JNIEnv* env, jclass clz,
		jshortArray pcmData, jint length);
JNIEXPORT void JNICALL JNIDEFINE(nativeCloseEncoder)(JNIEnv* env, jclass clz);
}
;

#define BUFFER_SIZE 8192

/***********************************************
 Define module variable
 ************************************************/
static lame_t lame;

JNIEXPORT jint JNICALL JNIDEFINE(nativeOpenEncoder)(JNIEnv* env, jclass clz) {

	lame = lame_init();
	lame_set_in_samplerate(lame, 44100);
	lame_set_brate(lame,128);
	//lame_set_num_channels(lame, 1);
	lame_set_VBR(lame, vbr_default);
	//lame_set_mode(lame, MONO);
	lame_set_quality(lame, 2);
	lame_init_params(lame);

	return 0;
}

JNIEXPORT void JNICALL JNIDEFINE(nativeCloseEncoder)(JNIEnv* env, jclass clz) {
	lame_close(lame);
	return;
}

JNIEXPORT jint JNICALL JNIDEFINE(nativeEncodingPCM)(JNIEnv* env, jclass clz,
		jbyteArray pcmData, jint pcmLength, jbyteArray mp3Data) {

	jbyte *pcm, *mp3;

	jboolean isCopy = false;
	pcm = env->GetByteArrayElements(pcmData, &isCopy);
	mp3 = env->GetByteArrayElements(mp3Data, &isCopy);
	int mp3Size = env->GetArrayLength(mp3Data);

	//int ret = lame_encode_buffer_interleaved(lame, (short *)pcm, pcmLength/2, (unsigned char *)mp3, mp3Size);
	int ret = lame_encode_buffer(lame, (short *) pcm, (short *) pcm, pcmLength / 2, (unsigned char *) mp3, mp3Size);

	env->ReleaseByteArrayElements(pcmData, pcm, JNI_ABORT); /*Don't copy to java side*/
	env->ReleaseByteArrayElements(mp3Data, mp3, 0); /*Copy to java side*/
	return ret;
}

JNIEXPORT jbyteArray JNICALL JNIDEFINE(nativeEncodingPCMv2)(JNIEnv* env, jclass clz,
		jshortArray pcmData, jint pcmLength) {

	int nb_write = 0;
	unsigned char output[BUFFER_SIZE];
	jboolean isCopy = false;

	jshort *input = env->GetShortArrayElements(pcmData, &isCopy);
	nb_write = lame_encode_buffer(lame, input, input, pcmLength, output, BUFFER_SIZE);
	jbyteArray result = env->NewByteArray(nb_write);
	env->SetByteArrayRegion(result, 0, nb_write, (jbyte *)output);

	env->ReleaseShortArrayElements(pcmData, input, JNI_ABORT);

	return result;
}

