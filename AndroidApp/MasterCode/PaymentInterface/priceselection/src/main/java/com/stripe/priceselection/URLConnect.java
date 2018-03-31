
package com.stripe.priceselection;

import java.net.*;
import java.io.*;
import java.io.InputStream;
import java.lang.*;

/**
 * Created by natalia on 01/03/18.
 */

public class URLConnect{

    public String GET() {
        // GET request
        URL url;
        HttpURLConnection urlConnection = null;
        char current;
        char[] c = new char[400];
        String s=null;

        try {
            url = new URL("http://159.89.118.48/app/");

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);


            int data = isw.read();
            int i = 0;
            while (data != -1) {
                current = (char) data;
                c[i] = current;
                data = isw.read();
                i++;
            }
            s = String.valueOf(c);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return s;
        }
    }
    public String POST(String data) {
        String responseString = null;
        try {
            String url = "http://159.89.118.48/app/post/";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java-Client");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "data="+data;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            responseString = response.toString();
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }  catch(Exception e) {
            e.printStackTrace();
        }
        return responseString;
    }
}