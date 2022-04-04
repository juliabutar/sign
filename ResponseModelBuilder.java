package go.pajak.pbb.app.registrasi.dtomodel;

public class ResponseModelBuilder {

    private ResponseModel response = new ResponseModel();

    public ResponseModelBuilder success (){

        response.setKodeResponse(1);
        response.setMessage("Sukses");
        return this;

    }

    public ResponseModelBuilder failed (){

        response.setKodeResponse(0);
        response.setMessage("Gagal");
        return this;

    }

    public ResponseModelBuilder withMessage (String message){

        response.setMessage(message);
        return this;

    }

    public ResponseModelBuilder withData (Object object){

        response.setObjResponse(object);
        return this;

    }

    public ResponseModel build (){

        return this.response;
    }

}
