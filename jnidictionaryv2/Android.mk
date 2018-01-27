LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := anysoftkey2_jni
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	C:\KeyLog5\jnidictionaryv2\src\main\jni\char_utils.cpp \
	C:\KeyLog5\jnidictionaryv2\src\main\jni\com_anysoftkeyboard_dictionaries_ResourceBinaryDictionary.cpp \
	C:\KeyLog5\jnidictionaryv2\src\main\jni\dictionary.cpp \

LOCAL_C_INCLUDES += C:\KeyLog5\jnidictionaryv2\src\main\jni
LOCAL_C_INCLUDES += C:\KeyLog5\jnidictionaryv2\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
