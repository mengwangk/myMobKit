LOCAL_PATH:= $(call my-dir)
MY_LOCAL_PATH = $(LOCAL_PATH)

###########################################################
# building dummy library
#
include $(CLEAR_VARS)
LOCAL_MODULE := libjingle_peerconnection_so
LOCAL_CPP_EXTENSION := .cc .cpp
LOCAL_CPPFLAGS := -O2 -Werror -Wall
LOCAL_CPPFLAGS += -DANDROID
LOCAL_C_INCLUDES :=  $(MY_LOCAL_PATH)
LOCAL_SRC_FILES := libjingle_peerconnection_dummy.cpp
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)