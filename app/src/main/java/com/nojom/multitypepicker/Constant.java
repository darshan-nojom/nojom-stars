package com.nojom.multitypepicker;

/**
 * Created by Vincent Woo
 * Date: 2016/10/14
 * Time: 17:20
 */

public interface Constant {
    String MAX_NUMBER = "MaxNumber";

    int REQUEST_CODE_PICK_IMAGE = 0x100;
    String RESULT_PICK_IMAGE = "ResultPickImage";
    int REQUEST_CODE_TAKE_IMAGE = 0x101;

    int REQUEST_CODE_BROWSER_IMAGE = 0x102;
    String RESULT_BROWSER_IMAGE = "ResultBrowserImage";

    int REQUEST_CODE_PICK_VIDEO = 0x200;
    String RESULT_PICK_VIDEO = "ResultPickVideo";
    int REQUEST_CODE_TAKE_VIDEO = 0x201;

    int REQUEST_CODE_PICK_AUDIO = 0x300;
    String RESULT_PICK_AUDIO = "ResultPickAudio";
    int REQUEST_CODE_TAKE_AUDIO = 0x301;

    int REQUEST_CODE_PICK_FILE = 0x400;
    String RESULT_PICK_FILE = "ResultPickFILE";

    int PIC_CROP = 121;
}
