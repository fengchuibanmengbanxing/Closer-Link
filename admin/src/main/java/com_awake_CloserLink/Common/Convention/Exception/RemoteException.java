package com_awake_CloserLink.Common.Convention.Exception;

/**
 * @Author 清醒
 * @Date 2024/5/14 11:42
 */
import com_awake_CloserLink.Common.Convention.ErrorCode.BaseErrorCode;
import com_awake_CloserLink.Common.Convention.ErrorCode.IErrorCode;

/**
 * 远程服务调用异常
 */
public class RemoteException extends AbstractException {

    public RemoteException(String message) {
        this(message, null, BaseErrorCode.REMOTE_ERROR);
    }

    public RemoteException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public RemoteException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}