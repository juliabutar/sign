package go.pajak.pbb.app.registrasi.service;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@Service
public class HcpService {

    @Value("${storage.url}")
    String urlHcp;
    @Value("${storage.secret}")
    String secret;

//    public static final String urlHcp = "http://map.development.objsdc.intranet.pajak.go.id/rest/";
//    public static final String username = "ns-dev-map";
//    public static final String password = "p@ssw0rd";

//    public static final String urlHcp = "http://pbb-p3.production.objsdc.intranet.pajak.go.id/rest/";
//    public static final String username = "ns-pbbp3";
//    public static final String password = "P@ssw0rdPBB";

    public HttpResponse writeHcp(String hcpFilePath, MultipartFile uplFile) throws Exception {
        //specify namespace URL - eg. ns01.tn01.hcp01.hitachi.com/rest/path  20
        String nameSpaceUrl = urlHcp + hcpFilePath;

        //create a new HttpClient object and a PUT request object  26
        HttpClient client = HttpClientBuilder.create().build();
        HttpPut request = new HttpPut(nameSpaceUrl);

        //add authorization header for user(base64) "exampleuser" with password(md5) "passw0rd"  30
        //request.addHeader("Authorization", getAuthorisation());
        request.addHeader("Authorization", secret);

        //setup byte array for file to upload(PUT)  34
        byte[] fileAsByteArr = uplFile.getBytes();
        ByteArrayEntity requestEntity = new ByteArrayEntity(fileAsByteArr);


        //InputStream is = new InputStream(uplFile.getInputStream());
        //set the request to use the byte array  40
//        InputStream fileStream = null;
//        fileStream = uplFile.getInputStream();

        request.setEntity(requestEntity);

        //execute PUT request  42
        HttpResponse response = client.execute(request);

        //print response status to console  45
        /*if (response.getStatusLine().getStatusCode() != 201) {
            throw new Exception("HCP Response Write Code : "
                    + response.getStatusLine().getStatusCode() + " "
                    + response.getStatusLine().getReasonPhrase());
        }*/
        return response;
    }

    public HttpResponse writeHcpFromByteArray(String hcpFilePath, byte[] doc) throws Exception {
        //specify namespace URL - eg. ns01.tn01.hcp01.hitachi.com/rest/path  20
        String nameSpaceUrl = urlHcp + hcpFilePath;

        //create a new HttpClient object and a PUT request object  26
        HttpClient client = HttpClientBuilder.create().build();
        HttpPut request = new HttpPut(nameSpaceUrl);

        //add authorization header for user(base64) "exampleuser" with password(md5) "passw0rd"  30
        //request.addHeader("Authorization", getAuthorisation());
        request.addHeader("Authorization", secret);

        //setup byte array for file to upload(PUT)  34
        //byte[] fileAsByteArr = uplFile.getBytes();
        //ByteArrayEntity requestEntity = new ByteArrayEntity(fileAsByteArr);

        ByteArrayEntity requestEntity = new ByteArrayEntity(doc);


        //InputStream is = new InputStream(uplFile.getInputStream());
        //set the request to use the byte array  40
//        InputStream fileStream = null;
//        fileStream = uplFile.getInputStream();

        request.setEntity(requestEntity);

        //execute PUT request  42
        HttpResponse response = client.execute(request);

        //print response status to console  45
        /*if (response.getStatusLine().getStatusCode() != 201) {
            throw new Exception("HCP Response Write Code : "
                    + response.getStatusLine().getStatusCode() + " "
                    + response.getStatusLine().getReasonPhrase());
        }*/
        return response;
    }

    public void writeHcp(String hcpFilePath, String fileLocationPath) throws Exception {
        //specify namespace URL - eg. ns01.tn01.hcp01.hitachi.com/rest/path  20
        String nameSpaceUrl = urlHcp + hcpFilePath;

        //create a new HttpClient object and a PUT request object  26
        HttpClient client = HttpClientBuilder.create().build();
        HttpPut request = new HttpPut(nameSpaceUrl);

        //add authorization header for user(base64) "exampleuser" with password(md5) "passw0rd"  30
        //request.addHeader("Authorization", getAuthorisation());
        request.addHeader("Authorization", secret);

        //setup byte array for file to upload(PUT)  34
        File input = new File(fileLocationPath);
        byte[] fileAsByteArr = convertFileToByteArray(input);
        ByteArrayEntity requestEntity = new ByteArrayEntity(fileAsByteArr);

        //set the request to use the byte array  40
        request.setEntity(requestEntity);

        //execute PUT request  42
        HttpResponse response = client.execute(request);

        //print response status to console  45
        if (response.getStatusLine().getStatusCode() != 201) {
            throw new Exception("HCP Response Write Code : "
                    + response.getStatusLine().getStatusCode() + " "
                    + response.getStatusLine().getReasonPhrase());
        }
    }

    public HttpResponse getFileHcp(String hcpFilePath) throws IOException {
        //specify namespace URL - eg. ns01.tn01.hcp01.HCP.hitachi.com/rest/path  2
        String url = urlHcp + hcpFilePath;

        //create a new HttpClient object and a GET request object  5
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        //add authorization header for user(base64) "exampleuser" with password(md5) "passw0rd"  9
        //request.addHeader("Authorization", getAuthorisation());
        request.addHeader("Authorization", secret);

        //execute the request  13
        HttpResponse response = client.execute(request);

        //print response status to console  16 -- 200 OK
        /*System.out.println("HCP Response getFile Code : "
                + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());*/

        return response;
        /*//print response content to console  25
        BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
        byte[] nm = response.getEntity().getContent().toString().getBytes();

        String filePathHcp = filePath + noTrx + "/" + nmFile;
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePathHcp)));
        int inByte;
        while ((inByte = bis.read()) != -1)
            bos.write(inByte);
        bis.close();
        bos.close();*/
    }

    public byte[] getFileInHcp(String pathFile) throws IOException{
        //create a new HttpClient object and a GET request object  5
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(urlHcp + pathFile);
        //add authorization header for user(base64) "exampleuser" with password(md5) "passw0rd"  9
        request.addHeader("Authorization", secret);
        //execute the request  13
        HttpResponse response = client.execute(request);
        //print response status to console  16
//        System.out.println("Response Code : " + pathFile + " " +
//                + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
        //print response content to console  25
        BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());

        byte[] bit = IOUtils.toByteArray(bis);
        return bit;
    }

    public HttpResponse delete(String hcpFilePath) throws Exception {
        String urlDelete = urlHcp + hcpFilePath;
        HttpClient clientDelete = HttpClientBuilder.create().build();
        HttpDelete delete = new HttpDelete(urlDelete);
        //delete.addHeader("Authorization", getAuthorisation());
        delete.addHeader("Authorization", secret);
        HttpResponse response = clientDelete.execute(delete);
        /*if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("HCP Response Delete Code : "
                    + response.getStatusLine().getStatusCode() + " "
                    + response.getStatusLine().getReasonPhrase());
        }*/
        return response;
    }

    public String[] getListFileName(String hcpFolderPath) throws IOException {
        //=============== GET LIST OF DIRECTORY HCP =============================
        String url = urlHcp + hcpFolderPath;

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        request.addHeader("Authorization", getAuthorisation());

        HttpResponse response = client.execute(request);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = "";
        int urut = 0;
        while ((line = rd.readLine()) != null) {
            urut++;
            System.out.println(urut + " " + line);
            if (line.contains("urlName")) {
                result.append(line);
            }
        }
        String[] name = result.toString().trim().split("<entry urlName=");
        return name;
    }

    private String getAuthorisation() {
//        String user = Base64.encodeBase64String(username.getBytes());
//        String pass = getMd5(password);
//        return "HCP " + user + ":" + pass;
        return "secret";

    }

    ;

    private String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            StringBuilder hashtext = new StringBuilder(no.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }
            return hashtext.toString();
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] convertFileToByteArray(File file) {
        FileInputStream fis = null;
        // Creating bytearray of same length as file
        byte[] bArray = new byte[(int) file.length()];
        try {
            fis = new FileInputStream(file);
            // Reading file content to byte array
            fis.read(bArray);
            fis.close();

        } catch (IOException ioExp) {
            ioExp.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bArray;
    }
}
