LOCAL_PATH:= $(call my-dir)
MY_LOCAL_PATH = $(LOCAL_PATH)

# building x264 library
LOCAL_MODULE    := libx264
LOCAL_SRC_FILES := x264/$(TARGET_ARCH_ABI)/libx264.a

$(info $(PREBUILT_STATIC_LIBRARY))

include $(PREBUILT_STATIC_LIBRARY)

# building x264 encoder
LOCAL_MODULE := libx264encoder
LOCAL_CPP_EXTENSION := .cc .cpp
LOCAL_CPPFLAGS := -O2 -Werror -Wall
LOCAL_C_INCLUDES :=  $(MY_LOCAL_PATH)
LOCAL_SRC_FILES := encoder.cpp \
                   h264encoder.cpp
LOCAL_LDLIBS += -llog -lz
LOCAL_SHARED_LIBRARIES := libcutils\
                          libgnustl\
                          libdl
LOCAL_STATIC_LIBRARIES := libx264
#LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
include $(BUILD_SHARED_LIBRARY)
