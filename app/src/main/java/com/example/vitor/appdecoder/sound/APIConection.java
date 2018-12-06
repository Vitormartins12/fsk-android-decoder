package com.example.vitor.appdecoder.sound;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.Buffer;

public class APIConection{

    public ArrayList<Integer> myList = new ArrayList<Integer>();


    public void logar(String mensagem) {
        if (mensagem != null) {
            try {
                Log.d("TAG", "teste :: " + mensagem);
                myList.add(Integer.parseInt(mensagem));
            }
            catch (Exception e ){
                Log.d("TAG", "sorry got an error");

            }
        }
    }

    public JSONObject mandar() throws ExecutionException, InterruptedException,JSONException {


        Integer myListSize = myList.size();
        if (myListSize > 0) {
            Integer lastelement = myList.get(myListSize -1);
            Log.d("TAG", String.valueOf(lastelement));
            File folder = new File("/storage");
            folder.mkdirs();
            File file = new File("/storage/emulated/0/medidas.txt");
            try {
                FileOutputStream fileinput = new FileOutputStream(file ,true);
                PrintStream printstream = new PrintStream(fileinput);
                printstream.print(myList+"\n");
                fileinput.close();

                String myUrl = String.format("https://fuel-app-rna.herokuapp.com/rna/%d",lastelement);
                String result;
                HttpGetRequest getRequest = new HttpGetRequest();
                result = getRequest.execute(myUrl).get();
                Log.d("TAG",result);
                JSONObject obj;
                obj = new JSONObject(result);
                return obj;

            } catch (Exception e) {
                e.printStackTrace();
                String result = "{'data':'Falha na conexão.'}";

                JSONObject obj;
                obj = new JSONObject(result);
                return obj;            }



        } else {
            Log.d("TAG", "not ready");
        }

        return null;
    }


}
