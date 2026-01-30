package sba301.fe.edu.vn.besba.base;

public abstract class BaseController {

    protected <T> BaseResponse<T> wrapSuccess(T data) {
        return BaseResponse.success(data);
    }

}
