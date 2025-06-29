package com.umc.pyeongsaeng.global.apiPayload.code.exception;

import com.umc.pyeongsaeng.global.apiPayload.code.BaseErrorCode;
import com.umc.pyeongsaeng.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

  private BaseErrorCode code;

  public ErrorReasonDTO getErrorReason() {
    return this.code.getReason();
  }

  public ErrorReasonDTO getErrorReasonHttpStatus(){
    return this.code.getReasonHttpStatus();
  }
}


