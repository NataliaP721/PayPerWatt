
package com.stripe.priceselection.activity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class allows the app to send GET and POST requests to the server, and to receive responses back.
 * @author Natalia Pavlovic
 * @version 3.0
 * @since March 20, 2018
 */
public class URLConnect{

    /**
     * Creates a HTTPURLconnection in order to send GET requests from the app to the server. Uses an inputStreamReader to read the
     * data sent back to the app from the server.
     */
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

    /**
     * Creates a HTTPURLconnection in order to send POST requests from the app to the server. Uses an inputStreamReader to read the
     * data sent back to the app from the server. Data in the POST requests is sent to the server as a String.
     */
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