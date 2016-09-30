# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)
MY_CORE_PATH := $(LOCAL_PATH)

###########################################################
#   mp3 encoder
#
include $(CLEAR_VARS)

LOCAL_MODULE := libmp3encoder
LOCAL_CFLAGS := -O2 -Wall -DANDROID -DSTDC_HEADERS -I./libmp3lame/ 

#including source files
#include $(LOCAL_PATH)/lame_mp3_wrapper_build.mk
include $(LOCAL_PATH)/libmp3lame_build.mk

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)


###########################################################
#   uPnP port mapping 
#
#include $(CLEAR_VARS)

#LOCAL_MODULE := libnatpmp
#LOCAL_CFLAGS := -O2 -Wall -DANDROID -DLINUX -I./libnatpmp/  

#including source files
#include $(LOCAL_PATH)/libnatpmp_build.mk

#LOCAL_LDLIBS := -llog

#include $(BUILD_SHARED_LIBRARY)   

##################################################################

# OpenCV

include $(CLEAR_VARS)

OPENCV_CAMERA_MODULES:=off
OPENCV_INSTALL_MODULES:=on
include $(LOCAL_PATH)/../../../../../OpenCV-3.1.0-android-sdk/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

#LOCAL_SRC_FILES  := DetectionBasedTracker_jni.cpp
#LOCAL_C_INCLUDES += $(LOCAL_PATH)
#LOCAL_LDLIBS     += -llog -ldl
#LOCAL_MODULE     := detection_based_tracker

#include $(BUILD_SHARED_LIBRARY)

#############################################################
# JPEG handler

#include $(CLEAR_VARS)
#
#LOCAL_SRC_FILES := JpegHandler.cpp
#LOCAL_C_INCLUDES := $(LOCAL_PATH)/libjpeg
#
#LOCAL_STATIC_LIBRARIES := libjpeg
#
#LOCAL_MODULE    := jpeghandler
#
#LOCAL_LDLIBS := -llog 
#
#include $(BUILD_SHARED_LIBRARY)
#
#include $(LOCAL_PATH)/libjpeg/Android.mk



###########################################################
#   Speex
#
#include $(CLEAR_VARS)

#LOCAL_CFLAGS := -DFIXED_POINT -DEXPORT="" -UHAVE_CONFIG_H -DUSE_KISS_FFT -DHAVE_SINF -DHAVE_TANF -DHAVE_COSF -DHAVE_ASINF -DHAVE_ATANF -DHAVE_ACOSF -DHAVE_ATAN2F -DHAVE_CEILF -DHAVE_FLOORF -DHAVE_POWF -DHAVE_LOG10F
#LOCAL_MODULE := speex
#including source files
#include $(LOCAL_PATH)/libspeex_build.mk
#include $(BUILD_SHARED_LIBRARY)

##################################################################


# libjpeg-turbo
include $(CLEAR_VARS)
LOCAL_STATIC_LIBRARIES := libjpeg-turbo
include $(MY_CORE_PATH)/libyuvimage/libjpeg-turbo/Android.mk

# YuvImage library
include $(CLEAR_VARS)
include $(MY_CORE_PATH)/libyuvimage/Android.mk

# libjingle library
#include $(CLEAR_VARS)
#include $(MY_CORE_PATH)/libjingle/Android.mk

# libx264encoder library
#include $(CLEAR_VARS)
#include $(MY_CORE_PATH)/libx264/Android.mk