LOCAL_PATH := $(call my-dir)

# $(info $(LOCAL_PATH))

include $(LOCAL_PATH)/Flags.mk

LOCAL_C_INCLUDES += $(LOCAL_PATH)/include/libjpeg
#LOCAL_C_INCLUDES += $(LOCAL_PATH)/libjpeg-turbo/libjpeg-turbo-1.4.1

LOCAL_MODULE    := yuvimage
LOCAL_SRC_FILES := yuvimage.cpp YuvToJpegEncoderMT.cpp
LOCAL_STATIC_LIBRARIES := jpeg-turbo gomp
LOCAL_SHARED_LIBRARIES := libandroid_runtime
LOCAL_LDLIBS := -llog

##LOCAL_LDLIBS := -llog \
##$(call host-path, $(LOCAL_PATH)/../prebuilt/$(TARGET_ARCH_ABI)/libandroid_runtime.so)

include $(BUILD_SHARED_LIBRARY)