LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := anysoftkey_jni
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	C:\KeyLog5\jnidictionaryv1\src\main\jni\com_anysoftkeyboard_dictionaries_BinaryDictionary.cpp \
	C:\KeyLog5\jnidictionaryv1\src\main\jni\dictionary.cpp \
	C:\KeyLog5\jnidictionaryv1\src\main\jni\makeLowerChars.py \
	C:\KeyLog5\jnidictionaryv1\src\main\jni\makeUnicodeMap.py \

LOCAL_C_INCLUDES += C:\KeyLog5\jnidictionaryv1\src\debug\jni
LOCAL_C_INCLUDES += C:\KeyLog5\jnidictionaryv1\src\main\jni

include $(BUILD_SHARED_LIBRARY)
