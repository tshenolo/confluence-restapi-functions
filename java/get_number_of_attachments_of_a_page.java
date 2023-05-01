/*
 * This PHP script will get the total number of attachments for a specific page via the Confluence REST API
 * 
 * Usage:
 * 
 * Update the following variables before you run this script:
 * - YOUR_SERVER
 * - YOUR_PERSONAL_ACCESS_TOKEN (found @ YOUR_SERVER/plugins/personalaccesstokens/usertokens.action
 * - YOUR_PAGEID 
 * 
 * Open your CLI and follow the steps below:
 * 
 * Execute the following command to compile your java code
 * javac -cp .\jar\javax.json-1.0.jar get_number_of_attachments_of_a_page.java
 * 
 * Execute the following command to get the number of attachments
 * java -cp ".;jar\javax.json-1.0.jar" get_number_of_attachments_of_a_page
 * 
 */


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.json.*;

public class get_number_of_attachments_of_a_page {
    private static final String REST_API = "YOUR_SERVER/rest/api/";
    private static final String PERSONAL_ACCESS_TOKEN = "YOUR_PERSONAL_ACCESS_TOKEN";

    public static void main(String[] args) throws IOException {
        String pageId = "YOUR_PAGEID";
        int totalAttachments = getNumberOfAttachments(pageId, 0);
        System.out.println("Total Attachments: " + totalAttachments);
    }

    private static int getNumberOfAttachments(String pageId, int start) throws IOException {
        String url = REST_API + "content/" + pageId + "/child/attachment?start="+start+"&limit=200";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Authorization", "Bearer " + PERSONAL_ACCESS_TOKEN);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");

        int size;
        try (InputStream inputStream = connection.getInputStream();
                JsonReader jsonReader = Json.createReader(inputStream)) {
            JsonObject content = jsonReader.readObject();
            size = content.getInt("size");
            //System.out.println("pageid: " + pageId+", size:"+size);
            if (content.containsKey("_links") && content.getJsonObject("_links").containsKey("next")) {
                size += getNumberOfAttachments(pageId, start + 200);
            }
        }

        return size;
    }
}
