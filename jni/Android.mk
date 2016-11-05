#
# Android NDK makefile 
#
# build - <ndk path>/ndk-build ICONV_SRC=<iconv library src> 
# clean -  <ndk path>/ndk-build clean
#
MY_LOCAL_PATH := $(call my-dir)

# libiconv
include $(CLEAR_VARS)
LOCAL_PATH := $(MY_LOCAL_PATH)

LOCAL_MODULE := libiconv

LOCAL_CFLAGS := \
    -Wno-multichar \
    -D_ANDROID \
    -DLIBDIR="c" \
    -DBUILDING_LIBICONV \
    -DBUILDING_LIBCHARSET \
    -DIN_LIBRARY \

LOCAL_CFLAGS += -fPIC 

LOCAL_SRC_FILES := \
	libiconv/lib/iconv.c \
	libiconv/libcharset/lib/localcharset.c \
	libiconv/lib/relocatable.c

LOCAL_C_INCLUDES := \
	$(MY_LOCAL_PATH)/libiconv/include \
	$(MY_LOCAL_PATH)/libiconv/libcharset \
	$(MY_LOCAL_PATH)/libiconv/libcharset/include

include $(BUILD_SHARED_LIBRARY)

LOCAL_LDLIBS := -llog -lcharset

# libzbarjni
include $(CLEAR_VARS)

LOCAL_PATH := $(MY_LOCAL_PATH)
LOCAL_MODULE := zbarjni
LOCAL_SRC_FILES := zbarjni.c \
		   libzbar/zbar/img_scanner.c \
		   libzbar/zbar/decoder.c \
		   libzbar/zbar/image.c \
		   libzbar/zbar/symbol.c \
		   libzbar/zbar/convert.c \
		   libzbar/zbar/config.c \
		   libzbar/zbar/scanner.c \
		   libzbar/zbar/error.c \
		   libzbar/zbar/refcnt.c \
		   libzbar/zbar/video.c \
		   libzbar/zbar/video/null.c \
		   libzbar/zbar/decoder/code128.c \
		   libzbar/zbar/decoder/code39.c \
		   libzbar/zbar/decoder/code93.c \
		   libzbar/zbar/decoder/codabar.c \
		   libzbar/zbar/decoder/databar.c \
		   libzbar/zbar/decoder/ean.c \
		   libzbar/zbar/decoder/i25.c \
		   libzbar/zbar/decoder/qr_finder.c \
		   libzbar/zbar/qrcode/bch15_5.c \
		   libzbar/zbar/qrcode/binarize.c \
		   libzbar/zbar/qrcode/isaac.c \
		   libzbar/zbar/qrcode/qrdec.c \
		   libzbar/zbar/qrcode/qrdectxt.c \
		   libzbar/zbar/qrcode/rs.c \
		   libzbar/zbar/qrcode/util.c

LOCAL_C_INCLUDES := $(MY_LOCAL_PATH)/libzbar/include \
		    $(MY_LOCAL_PATH)/libzbar/zbar \
		    $(MY_LOCAL_PATH)/libiconv/include 

LOCAL_SHARED_LIBRARIES := libiconv

include $(BUILD_SHARED_LIBRARY)