package com.hellosign.apisupport.hellosigntest;

import com.hellosign.sdk.HelloSignClient;
import com.hellosign.sdk.resource.Account;
import com.hellosign.sdk.resource.ApiApp;
import com.hellosign.sdk.HelloSignException;
import com.hellosign.sdk.resource.EmbeddedRequest;
import com.hellosign.sdk.resource.EmbeddedResponse;
import com.hellosign.sdk.resource.SignatureRequest;
import com.hellosign.sdk.resource.TemplateDraft;
import com.hellosign.sdk.resource.TemplateSignatureRequest;
import com.hellosign.sdk.resource.UnclaimedDraft;
import com.hellosign.sdk.resource.support.CustomField;
import com.hellosign.sdk.resource.support.Document;
import com.hellosign.sdk.resource.support.FormField;
import com.hellosign.sdk.resource.support.Signature;
import com.hellosign.sdk.resource.support.TemplateList;
import com.hellosign.sdk.resource.support.WhiteLabelingOptions;
import com.hellosign.sdk.resource.support.types.FieldType;
import com.hellosign.sdk.resource.support.types.UnclaimedDraftType;
import com.hellosign.sdk.resource.support.types.ValidationType;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import org.json.JSONException;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import io.split.client.SplitClient;
import io.split.client.SplitClientConfig;
import io.split.client.SplitFactory;
import io.split.client.SplitFactoryBuilder;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

/**
 *
 * @author alexgriffen NOTE TO SELF: you must be in /target to run this:
 * java -cp HelloSignTest-1.0-SNAPSHOT-jar-with-dependencies.jar com.hellosign.apisupport.hellosigntest.HStest
 *
 */
public class HStest {

    public static void main(String[] args) throws HelloSignException, IOException, JSONException, URISyntaxException {

        SplitClientConfig config = SplitClientConfig.builder()
                .setBlockUntilReadyTimeout(10000)
                .build();

        SplitFactory splitFactory = SplitFactoryBuilder.build("j8a7lefl8e8l08shnh8b54dmbaitdl5qk9la", config);
        SplitClient sp_client = splitFactory.client();

        try {
            sp_client.blockUntilReady();
        } catch (TimeoutException | InterruptedException e) {
            // log & handle
        }

        String treatment = sp_client.getTreatment("CUSTOMER_ID","NewUX");

        if (treatment.equals("on")) {

            while (true) {
                String apikey = System.getenv("HS_APIKEY_PROD");
                String clientid = System.getenv("HS_CLIENT_ID_PROD");
                String hstemplate = System.getenv("HS_TEMPLATE_ID");
                System.out.println("\nEnter:\n"
                        + "1 for account\n"
                        + "2 for sig req\n"
                        + "3 for embedded sig req\n"
                        + "4 for emebdded templates\n"
                        + "5 for embedded requesting for embedded signing\n"
                        + "6 for embedded requesting with template for embedded signing\n"
                        + "7 for embedded requesting for NON embedded signing\n"
                        + "8 for embedded signing with template\n"
                        + "9 for nonembedded signing with template\n"
                        + "10 for embedded sig with text tags\n"
                        + "11 to update the callback url of your app\n"
                        + "12 to get a signature request object response\n"
                        + "13 to trigger a loop where every few seconds, a signature request is generated from a template\n"
                        + "14 to create a new API App with White Labeling\n"
                        + "15 to check if an account is valid for oauth\n"
                        + "16 to hit /template/list endpoint\n"
                        + "17 to trigger an error response to GET /sign_url call\n"
                        + "18 for embedded signing with form_fields_per_document\n"
                        + "19 to use okhttp3 with unclaimed draft with template\n"
                        + "20 to trigger a loop where every few seconds, a signature request is generated from a template\n"
                        + "or 0 to exit: ");

                String localFile = "/Users/alexgriffen/Demos/JavaConsole/javaconsole-HelloSignAPI/nda.pdf";
                String localFile1 = "/Users/alexgriffen/Demos/JavaConsole/javaconsole-HelloSignAPI/TestingTextTagsvisible_signer0.pdf";
                String localTextTagsFile = "/Users/alexgriffen/Demos/JavaConsole/javaconsole-HelloSignAPI/TestingTextTagsvisible_signer0.pdf";
                String signer1 = "/Users/alexgriffen/Demos/JavaConsole/javaconsole-HelloSignAPI/editable_signer_text_tag.pdf";
                String localLogo = "/Users/alexgriffen/Demos/JavaConsole/javaconsole-HelloSignAPI/transparent_image.png";

                BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                String options = bufferRead.readLine();

                if (options.equals("1")) {
                    // this GETs the account object
                    System.out.print(apikey + "\n");
                    HelloSignClient client = new HelloSignClient(apikey);
                    Account account = client.getAccount();
                    System.out.println(account.toString(2));

                } else if (options.equals("2")) {

                    // non-embedded signature request
                    // yes this is sloppy and I'm not catching errors in the email address
                    // HelloSign will catch it when the POST goes out I think, and that's good enough for a simple console app imo
                    System.out.println("\nEnter the email address of the signer:\n");
                    BufferedReader bufferRead1 = new BufferedReader(new InputStreamReader(System.in));
                    String emailaddress = bufferRead1.readLine();
                    System.out.print("Enter the name of the signer: \n");
                    BufferedReader bufferRead2 = new BufferedReader(new InputStreamReader(System.in));
                    String signername = bufferRead2.readLine();
                    HelloSignClient client = new HelloSignClient(apikey);
                    SignatureRequest request = new SignatureRequest();
//                request.setSubject("NDA from JAVA");
                    request.setSubject("Subject Here");
                    request.setTitle("Title Here");
                    request.setTestMode(true);
                    request.setMessage("Triggered from Alex's JAVA SDK Integration");
                    request.addSigner(emailaddress, signername);
                    request.setRedirectUrl("https://google.com");
                    request.setIsDeclinable(Boolean.FALSE);
                    request.setClientId(clientid);

                    //request.addFile(new File(localFile));
                    request.addFile(new File(localFile));
                    request.addFile(new File(localFile1));
                    request.addFile(new File(localLogo));
                    // Prints the JSON response to the console
                    SignatureRequest response = client.sendSignatureRequest(request);

                    System.out.println(response.toString());

                    System.out.print("Signature Request sent! \n");

                } else if (options.equals("3")) {

                    //embedded signature request
                    SignatureRequest request = new SignatureRequest();
                    request.addFile(new File(localFile));
                    request.setSubject("My First embedded signature request"); //lol I did my first already, so this hardcoded 'subject' is a LIE and you'll never know
                    // muahhhhhh haaa haaa
                    // ^ evil laugh

                    request.setMessage("Awesome, right?");
                    request.addSigner("jack@example.com", "Jack");
                    request.addSigner("jill@example.com", "Jill");
                    request.setTestMode(true);

                    // String clientId = "d7219512693825facdd9241f458decf2";
                    // EmbeddedRequest embedReq = new EmbeddedRequest(clientId, request);
                    // leaving those two lines in case I want to make the clientID a choice later on
                    // replacing the clientID below with the clientid String from getenv("HS_CLIENT_ID_PROD")
                    EmbeddedRequest embedReq = new EmbeddedRequest(clientid, request);

                    HelloSignClient client = new HelloSignClient(apikey);
                    SignatureRequest newRequest = (SignatureRequest) client.createEmbeddedRequest(embedReq);
                    // get the signature_id of the first signer
                    // hardcoded because I'd rather not take the time to do this programmaticaly
                    // was thinking of a for or while loop with "1" or "2" for the two different signers
                    // and handle any entry that isn't "1" or "2"
                    // but to make that make sense, I'd have to keep the user in the loop until they
                    // had all of the URLs they wanted
                    // and for this simple example, that's more effort than I'd like to spend
                    // the webapp should include it though
                    Signature sigidFirstSigner = newRequest.getSignature("jack@example.com", "Jack");
                    String signID = sigidFirstSigner.getId();
                    System.out.print(signID + "\n");
                    System.out.print("Embedded Signature Request created! \n");

                    EmbeddedResponse embRequest = client.getEmbeddedSignUrl(signID);
                    String signUrl = embRequest.getSignUrl();
                    String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(signUrl, "UTF-8") + "&client_id=" + clientid;
                    System.out.println(url + "\n");

                } else if (options.equals("4")) {

                    // embedded templates
                    TemplateDraft draft = new TemplateDraft();
                    // String clientId = "d7219512693825facdd9241f458decf2";
                    // removint this for now, and using the getenv("HS_CLIENT_ID_PROD") instead
                    draft.setTestMode(true);
                    draft.setTitle("NDA Template");
                    draft.addSignerRole("Tenant1", 1);
                    draft.addSignerRole("Signer2", 2);
                    draft.setOrderMatters(true);
                    draft.addCCRole("Lawyer");
                    draft.addFile(new File(localFile));

                    EmbeddedRequest eReq = new EmbeddedRequest(clientid, draft);
                    HelloSignClient client = new HelloSignClient(apikey);
                    TemplateDraft t = client.createEmbeddedTemplateDraft(eReq);
                    String editUrl = t.getEditUrl();
                    String templateID = t.getId();
                    System.out.print(templateID + "\n");
                    String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(editUrl, "UTF-8") + "&client_id=" + clientid;
                    System.out.print(url + "\n");

                } else if (options.equals("5")) {

                    // unclaimed draft with embedded signing
                    // aka embedded requesting for embedded signing
                    SignatureRequest sigReq = new SignatureRequest();
                    sigReq.setTestMode(true);
                    sigReq.addFile(new File(localFile));
//                int docs = array[localFile];
//                sigReq.setDocuments();

                    UnclaimedDraft draft = new UnclaimedDraft(sigReq, UnclaimedDraftType.request_signature);
                    draft.setIsForEmbeddedSigning(true);
                    draft.setRequesterEmail("jolene@example.com");

                    // String clientId = "d7219512693825facdd9241f458decf2";
                    // removint this for now, and using the getenv("HS_CLIENT_ID_PROD") instead
                    EmbeddedRequest embedReq = new EmbeddedRequest(clientid, draft);

                    HelloSignClient client = new HelloSignClient(apikey);
                    UnclaimedDraft responseDraft = (UnclaimedDraft) client.createEmbeddedRequest(embedReq);
                    String claimUrl = responseDraft.getClaimUrl();
                    String signatureRequestId = responseDraft.getSignatureRequestId();
                    String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(claimUrl, "UTF-8") + "&client_id=" + clientid;
                    System.out.print(url + "\n" + signatureRequestId + "\n");

                } else if (options.equals("6")) {

                    // unclaimed draft for embedded signing using a template from the user
                    // aka embedded requesting with template
                    TemplateSignatureRequest tempsigReq = new TemplateSignatureRequest();
                    tempsigReq.setTestMode(true);
//                System.out.println("\nEnter the template id:\n");
//                BufferedReader bufferReadtid = new BufferedReader(new InputStreamReader(System.in));
//                String templateid = bufferReadtid.readLine();
                    String templateid = "4fbf53c4c064d5f3cd59c79a4fd8829c95e8f6ea";
                    tempsigReq.addTemplateId(templateid);
//                tempsigReq.setCC("Lawyer", "aleahahahahx+lawyer@hellosign.com");
//                tempsigReq.setSigner("Role0", "alex+tenant1@hellosign.com", "Alex JAVA Tenant0");
                    tempsigReq.setSigner("Role1", "alex+signer2@hellosign.com", "Alex JAVA Signer1");
                    tempsigReq.setSigner("Role2", "alex+signer2@hellosign.com", "Alex JAVA Signer2");
                    // tempsigReq.setOrderMatters(true);
                    tempsigReq.addFile(new File(localFile1));

//                tempsigReq.setUseTextTags(true);
                    UnclaimedDraft draft = new UnclaimedDraft(tempsigReq, UnclaimedDraftType.request_signature);
                    draft.setIsForEmbeddedSigning(true);
//                draft.setUseTextTags(true);
                    draft.setRequesterEmail("jolene@example.com");
//                draft.addFile(new File(localTextTagsFile));

//                 String clientId = "d7219512693825facdd9241f458decf2";
                    // removint this for now, and using the getenv("HS_CLIENT_ID_PROD") instead
                    EmbeddedRequest embedReq = new EmbeddedRequest(clientid, draft);
                    embedReq.addFile(new File(localFile1));

                    HelloSignClient client = new HelloSignClient(apikey);
                    System.out.print(apikey + "\n");
                    UnclaimedDraft responseDraft = (UnclaimedDraft) client.createEmbeddedRequest(embedReq);
                    String claimUrl = responseDraft.getClaimUrl();
                    String signatureID = responseDraft.getSignatureRequestId();
                    System.out.print("\nSignature Request id = " + signatureID + "\n");
                    String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(claimUrl, "UTF-8") + "&client_id=" + clientid;
                    System.out.print(url + "\n");

                } else if (options.equals("7")) {

                    //embedded requesting with NON embedded siging
                    SignatureRequest sigReq = new SignatureRequest();
                    sigReq.setTestMode(true);
                    sigReq.addFile(new File(localFile));

                    UnclaimedDraft draft = new UnclaimedDraft(sigReq, UnclaimedDraftType.request_signature);
                    draft.setIsForEmbeddedSigning(false);
                    draft.setRequesterEmail("alex+javaexample@hellosign.com");

                    // String clientId = "d7219512693825facdd9241f458decf2";
                    // removint this for now, and using the getenv("HS_CLIENT_ID_PROD") instead
                    EmbeddedRequest embedReq = new EmbeddedRequest(clientid, draft);

                    HelloSignClient client = new HelloSignClient(apikey);
                    UnclaimedDraft responseDraft = (UnclaimedDraft) client.createEmbeddedRequest(embedReq);
                    String claimUrl = responseDraft.getClaimUrl();
                    String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(claimUrl, "UTF-8") + "&client_id=" + clientid;
                    System.out.print(url + "\n");

                } else if (options.equals("8")) {

                    // embedded signing with template
                    // TODO make this take in a template id and boolean with custom fields
                    // SignatureResponse signatureResponse = newSigantureResponse();
                    // commenting out this section to test redirectUrl
                    TemplateSignatureRequest request = new TemplateSignatureRequest();
                    request.setTemplateId("5f15711bf170531c5336528a1a4cbad2bd10da41");
                    request.setSubject("Purchase Order");
                    request.setMessage("Glad we could come to an agreement.");

                    CustomField Cost = new CustomField();
                    Cost.setEditor("Role1");
                    Cost.setIsRequired(Boolean.TRUE);
                    Cost.setValue("$20,000");
                    Cost.setName("Cost");
                    request.addCustomField(Cost);
//                // CustomField CostAlso = new CustomField();
//                // CostAlso.setValue("$30,000");
//                // CostAlso.setName("Cost Also");
//                // request.addCustomField(CostAlso);
                    request.setSigner("Role1", "george@example.com", "George Franklin");
                    request.setSigner("Role2", "bob@bobl.com", "Bob Johnson");
                    request.setSigner("Role3", "frank@frank.com", "Frank Georgeson");
                    request.setSigner("Role4", "bobsson@bob.com", "BobsSon Johnson");
                    request.setSigner("Role5", "dkslf@dsflk.com", "lksjfl lksjdlak");

                    request.setTestMode(true);
                    request.addMetadata("things", "role");
                    request.addMetadata("lols", "role");
                    request.setRedirectUrl("https://google.com");

                    EmbeddedRequest embedReq = new EmbeddedRequest(clientid, request);
                    HelloSignClient client = new HelloSignClient(apikey);
                    SignatureRequest newRequest = (SignatureRequest) client.createEmbeddedRequest(embedReq);
//
                    Signature sigidRole1 = newRequest.getSignature("george@example.com", "George Franklin");
                    String signID = sigidRole1.getId();
                    System.out.print(signID + "\n");
                    EmbeddedResponse embRequest = client.getEmbeddedSignUrl(signID);
                    String signUrl = embRequest.getSignUrl();
                    String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(signUrl, "UTF-8") + "&client_id=" + clientid;
                    System.out.println(signUrl + "\n");
                    System.out.println(url + "\n");

                    String id = newRequest.getId();
                    System.out.print(id + " lol\n");
                    System.out.print(newRequest + "\n");

//                TemplateSignatureRequest request = new TemplateSignatureRequest();
//                request.setTemplateId("5f15711bf170531c5336528a1a4cbad2bd10da41");
//                request.setSubject("Purchase Order");
//                request.setMessage("Glad we could come to an agreement.");
//                request.setSigner("Role1", "george@example.com", "George Franklin");
//                request.setSigner("Role2", "bob@bobl.com", "Bob Johnson");
//                request.setSigner("Role3", "frank@frank.com", "Frank Georgeson");
//                request.setSigner("Role4", "bobsson@bob.com", "BobsSon Johnson");
//                request.setSigner("Role5", "dkslf@dsflk.com", "lksjfl lksjdlak");
////                request.setRedirectUrl("https://google.com");
//
//                request.setCustomFieldValue("Cost", "$20,000");
//                request.setTestMode(true);
//
//                HelloSignClient client = new HelloSignClient(apikey);
//                SignatureRequest response = client.sendTemplateSignatureRequest(request);
////                SignatureRequest response = client.sendTemplateSignatureRequest(request);
//
//                System.out.println(response.toString());
//
//                System.out.print("Signature Request sent! \n");
                } else if (options.equals("9")) {

                    // nonembedded signing with template
                    // TODO make this take in a template id and boolean with custom fields
                    // SignatureResponse signatureResponse = newSigantureResponse();
                    TemplateSignatureRequest request = new TemplateSignatureRequest();
                    request.setTemplateId(hstemplate);
                    request.setSubject("Purchase Order");
                    request.setMessage("Glad we could come to an agreement.");

                    CustomField Cost = new CustomField();
                    Cost.setEditor("Role1");
                    Cost.setIsRequired(Boolean.TRUE);
                    Cost.setValue("$20,000");
                    Cost.setName("Cost");
                    // CustomField CostAlso = new CustomField();
                    // CostAlso.setValue("$30,000");
                    // CostAlso.setName("Cost Also");
                    request.setSigner("Role1", "alex+george@hellosign.com", "George Franklin");
                    request.setSigner("Role2", "alex+bob@hellosign.com", "Bob Johnson");
                    request.setSigner("Role3", "alex+frank@hellosign.com", "Frank Georgeson");
                    request.setSigner("Role4", "alex+bobsson@hellosign.com", "BobsSon Johnson");
                    request.setSigner("Role5", "alex+lksjfl@hellosign.com", "lksjfl lksjdlak");
                    request.addCustomField(Cost);
                    // request.addCustomField(CostAlso);
                    request.setTestMode(true);
                    request.addMetadata("things", "role");
                    request.addMetadata("lols", "role");
                    request.setClientId(clientid);

                    HelloSignClient client = new HelloSignClient(apikey);
                    SignatureRequest newRequest = client.sendTemplateSignatureRequest(request);
                    String id = newRequest.getId();
                    System.out.print(id + "\n");
                    System.out.print(newRequest + "\n");

                } else if (options.equals("10")) {
                    //embedded signing with text tags
                    SignatureRequest request = new SignatureRequest();
                    request.addFile(new File(localTextTagsFile)); //one signer in this case, so my PDF has tags for signer1 only
                    request.setSubject("My First embedded signature request");
                    request.setMessage("Awesome, right?");
                    request.addSigner("jack@example.com", "Jack");
                    request.setTestMode(true);
                    request.setUseTextTags(true);
                    request.setHideTextTags(true);

                    EmbeddedRequest embedReq = new EmbeddedRequest(clientid, request);

                    HelloSignClient client = new HelloSignClient(apikey);

                    SignatureRequest newRequest = (SignatureRequest) client.createEmbeddedRequest(embedReq);

                    // get the signature_id of the first signer
                    // hardcoded because I'd rather not take the time to do this programmaticaly
                    Signature sigidFirstSigner = newRequest.getSignature("jack@example.com", "Jack");

                    String signID = sigidFirstSigner.getId();
                    System.out.print(signID + "\n");
                    System.out.print("Embedded Signature Request created! \n");

                    EmbeddedResponse embRequest = client.getEmbeddedSignUrl(signID);
                    String signUrl = embRequest.getSignUrl();
                    System.out.println(signUrl + "\n");
                    String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(signUrl, "UTF-8") + "&client_id=" + clientid;
                    System.out.println(url + "\n");

                } else if (options.equals("11")) {

                    HelloSignClient client = new HelloSignClient(apikey);
                    ApiApp app1 = client.getApiApp(clientid);

                    String callback_pre = app1.getCallbackUrl();
                    System.out.println(callback_pre + " is the callback on the app at this point\n");

                    System.out.println("\nEnter the new callback url for your app:\n");
                    BufferedReader bufferRead3 = new BufferedReader(new InputStreamReader(System.in));
                    String appCallbackURL = bufferRead3.readLine();

                    app1.setCallbackUrl(appCallbackURL);
                    client.updateApiApp(app1);

                    String callback = app1.getCallbackUrl();
                    System.out.println(callback + " is the new callback on the app");

                } else if (options.equals("12")) {

                    // GET signature request response object
                    System.out.println("\nEnter the signature_request_id of the request you'd like to see:\n");
                    BufferedReader bufferRead1 = new BufferedReader(new InputStreamReader(System.in));
                    String sigRequestId = bufferRead1.readLine();

                    HelloSignClient client = new HelloSignClient(apikey);
                    SignatureRequest request = client.getSignatureRequest(sigRequestId);

                    System.out.println(request + "\n");

                    Object filesUrl = client.getFilesUrl(sigRequestId);

                    System.out.println(filesUrl + "\n");

                } else if (options.equals("13")) {
                    int i = 0;
                    while (true) { //just an always-true statement to keep the while loop running
                        try {
                            System.out.println("Starting loop. Enter ctrl+c to break.\n");
                            TemplateSignatureRequest request = new TemplateSignatureRequest();
                            request.setTemplateId("8b52650dbde7846df1287de6617803cca7eb6bde");
                            request.setSubject("Purchase Order");
                            request.setMessage("Glad we could come to an agreement.");

//                        CustomField manager_name = new CustomField();
//                        manager_name.setValue("Bob Bobson");
//                        manager_name.setName("manager_name");
//                        request.addCustomField(manager_name);
//
//                        CustomField customer_name = new CustomField();
//                        customer_name.setValue("Frank Bobson");
//                        customer_name.setName("customer_name");
//                        request.addCustomField(customer_name);
//
//                        CustomField account_name = new CustomField();
//                        account_name.setValue("Account Bobson");
//                        account_name.setName("account_name");
//                        request.addCustomField(account_name);
//
//                        CustomField street = new CustomField();
//                        street.setValue("Account Bobson");
//                        street.setName("street");
//                        request.addCustomField(street);
//
//                        CustomField city = new CustomField();
//                        city.setValue("City Bobson");
//                        city.setName("city");
//                        request.addCustomField(city);
//
//                        CustomField state = new CustomField();
//                        state.setValue("State Bobson");
//                        state.setName("state");
//                        request.addCustomField(state);
//
//                        CustomField zip = new CustomField();
//                        zip.setValue("zip Bobson");
//                        zip.setName("zip");
//                        request.addCustomField(zip);
//
//                        CustomField phone = new CustomField();
//                        phone.setValue("phone Bobson");
//                        phone.setName("phone");
//                        request.addCustomField(phone);
//
//                        CustomField a1 = new CustomField();
//                        a1.setValue("phone Bobson");
//                        a1.setName("a1");
//                        request.addCustomField(a1);
//
//                        CustomField a2 = new CustomField();
//                        a2.setValue("phone Bobson");
//                        a2.setName("a2");
//                        request.addCustomField(a2);
//
//                        CustomField a3 = new CustomField();
//                        a3.setValue("phone Bobson");
//                        a3.setName("a3");
//                        request.addCustomField(a3);
//
//                        CustomField a4 = new CustomField();
//                        a4.setValue("phone Bobson");
//                        a4.setName("a4");
//                        request.addCustomField(a4);
//
//                        CustomField a5 = new CustomField();
//                        a5.setValue("phone Bobson");
//                        a5.setName("a5");
//                        request.addCustomField(a5);
//
//                        CustomField a6 = new CustomField();
//                        a6.setValue("phone Bobson");
//                        a6.setName("a6");
//                        request.addCustomField(a6);
//
//                        CustomField a7 = new CustomField();
//                        a7.setValue("phone Bobson");
//                        a7.setName("a7");
//                        request.addCustomField(a7);
//
//                        CustomField chk = new CustomField();
//                        chk.setValue("true");
//                        chk.setName("chk");
//                        request.addCustomField(chk);
//
//                        CustomField chk1 = new CustomField();
//                        chk1.setValue("true");
//                        chk1.setName("chk1");
//                        request.addCustomField(chk1);
//
//                        CustomField chk2 = new CustomField();
//                        chk2.setValue("true");
//                        chk2.setName("chk2");
//                        request.addCustomField(chk2);
//
//                        CustomField chk3 = new CustomField();
//                        chk3.setValue("true");
//                        chk3.setName("chk3");
//                        request.addCustomField(chk3);

                            request.setSigner("Role2", "frank@example.com", "Frank Franksonridge");
                            request.setSigner("Role3", "bob@example.com", "Bob Johnson");
                            request.setSigner("Role4", "Susan@example.com", "Susan Franksonridge");
                            request.setSigner("Role1", "Barbara@example.com", "Barbara Franksonridge");

//                        request.setTestMode(true);
                            request.addMetadata("things", "rolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerole");
                            request.addMetadata("lols", "rroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleole");
                            request.addMetadata("things1", "rolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerole");
                            request.addMetadata("lols1", "rolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerole");
                            request.addMetadata("things2", "rolesdfsdf");
                            request.addMetadata("lols2", "rosdfsdfasdflerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdf");
                            request.addMetadata("things3", "rofasdfsadflerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdf");
                            request.addMetadata("lols3", "rolerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdf");
                            request.addMetadata("things4", "rolerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdf");
                            request.addMetadata("lols4", "rolerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdf");

                            request.setRedirectUrl("https://google.com");

                            EmbeddedRequest embedReq = new EmbeddedRequest(clientid, request);
                            HelloSignClient client = new HelloSignClient(apikey);
                            SignatureRequest newRequest = (SignatureRequest) client.createEmbeddedRequest(embedReq);
                            //
                            Signature sigidRole1 = newRequest.getSignature("frank@example.com", "Frank Franksonridge");
                            String signID = sigidRole1.getId();
                            System.out.print(signID + "\n");

                            System.out.println("\nWaiting for 2 seconds to get the sign_url\n");
                            TimeUnit.SECONDS.sleep(2);

                            EmbeddedResponse embRequest = client.getEmbeddedSignUrl(signID);
                            String signUrl = embRequest.getSignUrl();
                            String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(signUrl, "UTF-8") + "&client_id=" + clientid;
                            System.out.println(signUrl + "\n");
                            System.out.println(url + "\n");

                            String id = newRequest.getId();
                            System.out.print(id + " lol\n");
                            System.out.print(newRequest + "\n");

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            System.out.println(dtf.format(now)); //2016/11/16 12:08:43

                            i++;
                            System.out.println(i + " is the number of times this has run");

//                        System.out.println("\nWaiting for 2 seconds...\n");
//                        TimeUnit.SECONDS.sleep(2);
//                        System.out.println("\nWaiting for 5 minutes...\n");
//                        TimeUnit.MINUTES.sleep(5);
                        } catch (InterruptedException e) {
                            System.out.println(e);
                        }

                    }

                } else if (options.equals("14")) {
                    // create a new Api App with white labeling

                    ApiApp app = new ApiApp();

                    app.setCallbackUrl("https://hstests.ngrok.io/callback.php");
                    app.setCustomLogo(new File(localLogo));
//                app.setHeaderBackgroundColor("#00b3e6");
                    app.setPrimaryButtonColor("#00b3e6");
                    app.setDomain("ngrok.io");
                    LocalDateTime now = LocalDateTime.now();
                    app.setName(now + " is the name");
//                app.setWhiteLabelingOptions(options);
                    // CustomField CostAlso = new CustomField();
                    // CostAlso.setValue("$30,000");
                    // CostAlso.setName("Cost Also");

//                WhiteLabelingOptions wLoptions = new WhiteLabelingOptions();
//                wLoptions.setHeaderBackgroundColor("#efefef");
//                wLoptions.set
                    HelloSignClient client = new HelloSignClient(apikey);
                    ApiApp newApp = (ApiApp) client.createApiApp(app);

                    String client_id = newApp.getClientId();
                    String primaryButton = newApp.getWhiteLabelingOptions().getPrimaryButtonColor();

                    System.out.println(client_id + " is the new client_id and " + primaryButton + " is the new primary button color\n");

                    System.out.println("\nEnter 1 if you'd like to delete the new client_id, or\n2 if you'd like to update the white_labeling_options");
                    BufferedReader keepOrUpdate = new BufferedReader(new InputStreamReader(System.in));
                    String keepAOrDeleteOrUpdate = keepOrUpdate.readLine();
                    if (keepAOrDeleteOrUpdate.equals("1")) {
                        client.deleteApiApp(client_id);
                    } else if (keepAOrDeleteOrUpdate.equals("2")) {
                        WhiteLabelingOptions updatedOptions = new WhiteLabelingOptions();
                        updatedOptions.setPrimaryButtonTextColor("#ffffff");
                        updatedOptions.setHeaderBackgroundColor("#14213E");
                        updatedOptions.setPrimaryButtonColor("#C3A86A");
                        updatedOptions.setPrimaryButtonTextColor("#0e1a34");
                        updatedOptions.setPrimaryButtonHoverColor("#ffffff");
                        updatedOptions.setSecondaryButtonColor("#ffffff");
                        updatedOptions.setSecondaryButtonTextColor("#0e1a34");
                        updatedOptions.setSecondaryButtonHoverColor("#0e1a34");
                        updatedOptions.setSecondaryButtonTextHoverColor("#ffffff");
                        updatedOptions.setLinkColor("#0e1a34");
                        updatedOptions.setTextColor1("#464646");
                        newApp.setWhiteLabelingOptions(updatedOptions);
                        ApiApp updatedAppRes = client.updateApiApp(newApp);

                        String primaryButtonUpdated = updatedAppRes.getWhiteLabelingOptions().getPrimaryButtonColor();
                        System.out.println(primaryButtonUpdated + " should equal #C3A86A and should be different from the first app's response above\n");
                    }

                } else if (options.equals("15")) {
                    // check if an account is valid for oauth

                    System.out.println("\nEnter the email address of the account that you'd like to check:\n");
                    BufferedReader emailAddress = new BufferedReader(new InputStreamReader(System.in));
                    String accountEmail = emailAddress.readLine();
                    HelloSignClient client = new HelloSignClient(apikey);
                    if (client.isAccountValid(accountEmail)) {
                        // Account is valid
                        System.out.println("\nThat account is valid.\n Please use this link to authorize oAuth:\n https://app.hellosign.com/oauth/authorize?response_type=code&client_id=2d9e5cbc5d888bef3253c0489d6851f5&state=somethingrandom");

                    }

                } else if (options.equals("16")) {
                    // GET /template list response

                    TemplateList templateList = (new HelloSignClient(apikey)).getTemplates();
//                URL url = new URL("https://:%40api.hellosign.com/v3/template/list");
//                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                con.setRequestMethod("GET");
//                int status = con.getResponseCode();
//                BufferedReader in = new BufferedReader(
//                    new InputStreamReader(con.getInputStream()));
//                String inputLine;
//                StringBuffer content = new StringBuffer();
//                while ((inputLine = in.readLine()) != null) {
//                    content.append(inputLine);
//                }
//                in.close();
////                con.disconnect();
//                OkHttpClient client = new OkHttpClient();
//
//                Request request = new Request.Builder()
//                        .url("https://api.hellosign.com/v3/template/list")
//                        .get()
//                        .addHeader("authorization", "Basic =")
//                        .addHeader("cache-control", "no-cache")
//                        .build();
//
//                Response response = client.newCall(request).execute();
//
////                System.out.print("\n" + status + "\n");
//                System.out.print(response.body().string());
                    System.out.print("\n" + templateList);
                    System.out.print("\n");

                } else if (options.equals("17")) {
                    HelloSignClient client = new HelloSignClient(apikey);

                    String signatureId = "50e3542f738adfa7ddd4cbd4c00d2a8ab6e4194b";

                    try {
                        EmbeddedResponse response = client.getEmbeddedSignUrl(signatureId);
//                    String responseCode = response.getStatus();
                        System.out.print(response + "\n this is the response");
                    } catch (HelloSignException ex) {
                        System.out.print("\nthis is the exeption http code " + ex.getHttpCode());
                    }

                } else if (options.equals("18")) {
                    //embedded signature request with form_fields_per_docucment
                    SignatureRequest request = new SignatureRequest();
                    Document doc = new Document();
                    doc.setFile(new File(localFile)); //one signer in this case, so my PDF has tags for signer1 only
                    request.setSubject("java - form fields per document");
                    request.setMessage("Awesome, right?");
                    request.addSigner("jack@example.com", "Jack");
                    request.setTestMode(true);
                    FormField textField = new FormField();
                    textField.setType(FieldType.text);
                    textField.setName("First Name"); // Displayed to the signer as the "Field Label"
                    textField.setValidationType(ValidationType.letters_only);
                    textField.setSigner(0); // Signer indexes are zero-based
                    textField.setHeight(25);
                    textField.setWidth(300);
                    textField.setIsRequired(true);
                    textField.setPage(1); // 1-based indexing, relative to the document
                    textField.setX(100);
                    textField.setY(100);

                    doc.addFormField(textField);
                    request.addDocument(doc);

                    EmbeddedRequest embedReq = new EmbeddedRequest(clientid, request);

                    HelloSignClient client = new HelloSignClient(apikey);

                    SignatureRequest newRequest = (SignatureRequest) client.createEmbeddedRequest(embedReq);

                    // get the signature_id of the first signer
                    // hardcoded because I'd rather not take the time to do this programmaticaly
                    Signature sigidFirstSigner = newRequest.getSignature("jack@example.com", "Jack");

                    String signID = sigidFirstSigner.getId();
                    System.out.print(signID + "\n");
                    System.out.print("Embedded Signature Request created! \n");

                    EmbeddedResponse embRequest = client.getEmbeddedSignUrl(signID);
                    String signUrl = embRequest.getSignUrl();
                    System.out.println(signUrl + "\n");
                    String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(signUrl, "UTF-8") + "&client_id=" + clientid;
                    System.out.println(url + "\n");

                } else if (options.equals("19")) {
//                OkHttpClient client = new OkHttpClient();
//
//                RequestBody requestBody = new MultipartBuilder()
//                        .type(MultipartBuilder.FORM)
//                        .addFormDataPart("file", file.getName(),
//                                RequestBody.create(MediaType.parse("text/csv"), file))
//                        .addFormDataPart("some-field", "some-value")
//                        .build();
//                MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
//                RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"client_id\"\r\n\r\nd7219512693825facdd9241f458decf2\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"template_id\"\r\n\r\n4fbf53c4c064d5f3cd59c79a4fd8829c95e8f6ea\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"test_mode\"\r\n\r\n1\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"requester_email_address\"\r\n\r\nalex+postman@hellosign.com\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"is_for_embedded_signing\"\r\n\r\n1\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"signers[Role1][name]\"\r\n\r\nnameRole1\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"signers[Role1][email_address]\"\r\n\r\nalex+role1@hellosign.com\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
//                Request request = new Request.Builder()
//                        .url("https://api.hellosign.com/v3/unclaimed_draft/create_embedded_with_template")
//                        .post(body)
//                        .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
//                        .addHeader("Cache-Control", "no-cache")
//                        .addHeader("Authorization", Credentials.basic(apikey, ""))
//                        .build();
//
//                try (Response response = client.newCall(request).execute()) {
//                    String jsonData = response.body().string();
//                    System.out.print(jsonData + " is the whole response\n");
//                    JSONObject Jobject = new JSONObject(jsonData);
//                    String claim_url = Jobject.getJSONObject("unclaimed_draft").getString("claim_url");
//                    System.out.print(claim_url + " is the claim_url\n");
//                }

//                HttpResponse<String> response = Unirest.post("https://ff9e5cec91c827603d2669318a5432dbfa0268b04e0cb6ec34276da7c12f1246@api.hellosign.com/v3/unclaimed_draft/create_embedded_with_template")
//                        .header("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
//                        .header("Cache-Control", "no-cache")
//                        .body("------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"client_id\"\r\n\r\nd7219512693825facdd9241f458decf2\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"template_id\"\r\n\r\n4fbf53c4c064d5f3cd59c79a4fd8829c95e8f6ea\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"test_mode\"\r\n\r\n1\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"requester_email_address\"\r\n\r\nalex+postman@hellosign.com\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"is_for_embedded_signing\"\r\n\r\n1\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"signers[Role1][name]\"\r\n\r\nnameRole1\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"signers[Role1][email_address]\"\r\n\r\nalex+role1@hellosign.com\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"file[0]\"; filename=\"CreditAuth1.pdf\"\r\nContent-Type: application/pdf\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--")
//                        .asString();
                } else if (options.equals("20")) {
                    int i = 0;
                    while (true) { //just an always-true statement to keep the while loop running
                        try {
                            System.out.println("Starting loop. Enter ctrl+c to break.\n");
                            TemplateSignatureRequest request = new TemplateSignatureRequest();
                            request.setTemplateId("8b52650dbde7846df1287de6617803cca7eb6bde");
                            request.setSubject("Purchase Order");
                            request.setMessage("Glad we could come to an agreement.");

//                        CustomField manager_name = new CustomField();
//                        manager_name.setValue("Bob Bobson");
//                        manager_name.setName("manager_name");
//                        request.addCustomField(manager_name);
//
//                        CustomField customer_name = new CustomField();
//                        customer_name.setValue("Frank Bobson");
//                        customer_name.setName("customer_name");
//                        request.addCustomField(customer_name);
//
//                        CustomField account_name = new CustomField();
//                        account_name.setValue("Account Bobson");
//                        account_name.setName("account_name");
//                        request.addCustomField(account_name);
//
//                        CustomField street = new CustomField();
//                        street.setValue("Account Bobson");
//                        street.setName("street");
//                        request.addCustomField(street);
//
//                        CustomField city = new CustomField();
//                        city.setValue("City Bobson");
//                        city.setName("city");
//                        request.addCustomField(city);
//
//                        CustomField state = new CustomField();
//                        state.setValue("State Bobson");
//                        state.setName("state");
//                        request.addCustomField(state);
//
//                        CustomField zip = new CustomField();
//                        zip.setValue("zip Bobson");
//                        zip.setName("zip");
//                        request.addCustomField(zip);
//
//                        CustomField phone = new CustomField();
//                        phone.setValue("phone Bobson");
//                        phone.setName("phone");
//                        request.addCustomField(phone);
//
//                        CustomField a1 = new CustomField();
//                        a1.setValue("phone Bobson");
//                        a1.setName("a1");
//                        request.addCustomField(a1);
//
//                        CustomField a2 = new CustomField();
//                        a2.setValue("phone Bobson");
//                        a2.setName("a2");
//                        request.addCustomField(a2);
//
//                        CustomField a3 = new CustomField();
//                        a3.setValue("phone Bobson");
//                        a3.setName("a3");
//                        request.addCustomField(a3);
//
//                        CustomField a4 = new CustomField();
//                        a4.setValue("phone Bobson");
//                        a4.setName("a4");
//                        request.addCustomField(a4);
//
//                        CustomField a5 = new CustomField();
//                        a5.setValue("phone Bobson");
//                        a5.setName("a5");
//                        request.addCustomField(a5);
//
//                        CustomField a6 = new CustomField();
//                        a6.setValue("phone Bobson");
//                        a6.setName("a6");
//                        request.addCustomField(a6);
//
//                        CustomField a7 = new CustomField();
//                        a7.setValue("phone Bobson");
//                        a7.setName("a7");
//                        request.addCustomField(a7);
//
//                        CustomField chk = new CustomField();
//                        chk.setValue("true");
//                        chk.setName("chk");
//                        request.addCustomField(chk);
//
//                        CustomField chk1 = new CustomField();
//                        chk1.setValue("true");
//                        chk1.setName("chk1");
//                        request.addCustomField(chk1);
//
//                        CustomField chk2 = new CustomField();
//                        chk2.setValue("true");
//                        chk2.setName("chk2");
//                        request.addCustomField(chk2);
//
//                        CustomField chk3 = new CustomField();
//                        chk3.setValue("true");
//                        chk3.setName("chk3");
//                        request.addCustomField(chk3);

                            request.setSigner("Role1", "frank@example.com", "Frank Franksonridge");
                            request.setSigner("Role2", "bob@example.com", "Bob Johnson");
                            request.setSigner("Role3", "Susan@example.com", "Susan Franksonridge");
                            request.setSigner("Role4", "Barbara@example.com", "Barbara Franksonridge");

//                        request.setTestMode(true);
                            request.addMetadata("things", "rolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerole");
                            request.addMetadata("lols", "rroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleroleole");
                            request.addMetadata("things1", "rolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerole");
                            request.addMetadata("lols1", "rolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerolerole");
                            request.addMetadata("things2", "rolesdfsdf");
                            request.addMetadata("lols2", "rosdfsdfasdflerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdf");
                            request.addMetadata("things3", "rofasdfsadflerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdf");
                            request.addMetadata("lols3", "rolerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdf");
                            request.addMetadata("things4", "rolerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdf");
                            request.addMetadata("lols4", "rolerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolerolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdfrolesdfsdf");

                            request.setRedirectUrl("https://google.com");

                            EmbeddedRequest embedReq = new EmbeddedRequest(clientid, request);
                            HelloSignClient client = new HelloSignClient(apikey);
                            SignatureRequest newRequest = (SignatureRequest) client.createEmbeddedRequest(embedReq);
                            //
                            Signature sigidRole1 = newRequest.getSignature("frank@example.com", "Frank Franksonridge");
                            String signID = sigidRole1.getId();
                            System.out.print(signID + "\n");

                            System.out.println("\nWaiting for 2 seconds to get the sign_url\n");
                            TimeUnit.SECONDS.sleep(2);

                            EmbeddedResponse embRequest = client.getEmbeddedSignUrl(signID);
                            String signUrl = embRequest.getSignUrl();
                            String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(signUrl, "UTF-8") + "&client_id=" + clientid;
                            System.out.println(signUrl + "\n");
                            System.out.println(url + "\n");

                            String id = newRequest.getId();
                            System.out.print(id + " lol\n");
                            System.out.print(newRequest + "\n");

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            System.out.println(dtf.format(now)); //2016/11/16 12:08:43

                            i++;
                            System.out.println(i + " is the number of times this has run");

//                        System.out.println("\nWaiting for 2 seconds...\n");
//                        TimeUnit.SECONDS.sleep(2);
//                        System.out.println("\nWaiting for 5 minutes...\n");
//                        TimeUnit.MINUTES.sleep(5);
                        } catch (InterruptedException e) {
                            System.out.println(e);
                        }

                    }

                } else if (options.equals("0")) {
                    break;
                } else {
                    System.out.println("That was NOT an option - get it together, yo! \n");
                }
            }

            System.out.println("Got your quit command - gimme a minute to close out... \n");
            sp_client.destroy();
            System.exit(0);

        } else if (treatment.equals("off")) {

            while (true) {
                String apikey = System.getenv("HS_APIKEY_PROD");
                String clientid = System.getenv("HS_CLIENT_ID_PROD");
                String hstemplate = System.getenv("HS_TEMPLATE_ID");
                System.out.println("\nEnter:\n"
                        + "1 for account only - this is split 'Off'\n"
                        + "or 0 to exit: ");

                BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                String options = bufferRead.readLine();

                if (options.equals("1")) {
                    // this GETs the account object
                    System.out.print(apikey + "\n");
                    HelloSignClient client = new HelloSignClient(apikey);
                    Account account = client.getAccount();
                    System.out.println(account.toString(2));

                } else if (options.equals("0")) {
                    break;
                } else {
                    System.out.println("That was NOT an option - get it together, yo! \n");
                }
            }

            System.out.println("Got your quit command - gimme a minute to close out... \n");
            sp_client.destroy();
            System.exit(0);

        } else {
            // insert your control treatment code here
            while (true) {
                String apikey = System.getenv("HS_APIKEY_PROD");
                String clientid = System.getenv("HS_CLIENT_ID_PROD");
                String hstemplate = System.getenv("HS_TEMPLATE_ID");
                System.out.println("\nEnter:\n"
                        + "1 for account only - this is no status in split\n"
                        + "or 0 to exit: ");

                BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                String options = bufferRead.readLine();

                if (options.equals("1")) {
                    // this GETs the account object
                    System.out.print(apikey + "\n");
                    HelloSignClient client = new HelloSignClient(apikey);
                    Account account = client.getAccount();
                    System.out.println(account.toString(2));

                } else if (options.equals("0")) {
                    break;
                } else {
                    System.out.println("That was NOT an option - get it together, yo! \n");
                }
            }

            System.out.println("Got your quit command - gimme a minute to close out... \n");
            sp_client.destroy();
            System.exit(0);

        }

    }
}
