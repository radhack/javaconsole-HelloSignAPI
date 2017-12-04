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
import com.hellosign.sdk.resource.support.Signature;
import com.hellosign.sdk.resource.support.types.UnclaimedDraftType;

import java.io.*;
import java.net.URLEncoder;
import org.json.JSONException;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author alexgriffen NOTE TO SELF: you must be in /target to run this java -cp
 * HelloSignTest-1.0-SNAPSHOT-jar-with-dependencies.jar
 * com.hellosign.apisupport.hellosigntest.HStest
 */
public class HStest {

    public static void main(String[] args) throws HelloSignException, IOException, JSONException {

        while (true) {
            String apikey = System.getenv("HS_APIKEY_PROD");
            String clientid = System.getenv("HS_CLIENT_ID_PROD");
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
                    + "13 to trigger a loop where every 5 minutes, a signature request is generated\n"
                    + "14 to create a new API App with White Labeling\n"
                    + "15 to check if an account is valid for oauth\n"
                    + "or 0 to exit: ");
            
            String localFile = "/Users/alexgriffen/NetBeansProjects/HelloSignTest/nda.pdf";
            String localTextTagsFile = "/Users/alexgriffen/NetbeansProjects/HelloSignTest/TestingTextTagsvisible_signer0.pdf";
            String localLogo = "/Users/alexgriffen/NetBeansProjects/HelloSignTest/transparent_image.png";
            
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

                UnclaimedDraft draft = new UnclaimedDraft(sigReq, UnclaimedDraftType.request_signature);
                draft.setIsForEmbeddedSigning(true);
                draft.setRequesterEmail("jolene@example.com");

                // String clientId = "d7219512693825facdd9241f458decf2";
                // removint this for now, and using the getenv("HS_CLIENT_ID_PROD") instead
                EmbeddedRequest embedReq = new EmbeddedRequest(clientid, draft);

                HelloSignClient client = new HelloSignClient(apikey);
                UnclaimedDraft responseDraft = (UnclaimedDraft) client.createEmbeddedRequest(embedReq);
                String claimUrl = responseDraft.getClaimUrl();
                String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(claimUrl, "UTF-8") + "&client_id=" + clientid;
                System.out.print(url + "\n");

            } else if (options.equals("6")) {

                // unclaimed draft for embedded signing using a template from the user
                // aka embedded requesting with template
                TemplateSignatureRequest tempsigReq = new TemplateSignatureRequest();
                tempsigReq.setTestMode(true);
                System.out.println("\nEnter the template id:\n");
                BufferedReader bufferReadtid = new BufferedReader(new InputStreamReader(System.in));
                String templateid = bufferReadtid.readLine();
                tempsigReq.addTemplateId(templateid);
                tempsigReq.setCC("Lawyer", "alex+lawyer@hellosign.com");
                tempsigReq.setSigner("Tenant1", "alex+tenant1@hellosign.com", "Alex JAVA Tenant1");
                tempsigReq.setSigner("Signer2", "alex+signer2@hellosign.com", "Alex JAVA Signer2");
                // tempsigReq.setOrderMatters(true);

                UnclaimedDraft draft = new UnclaimedDraft(tempsigReq, UnclaimedDraftType.request_signature);
                draft.setIsForEmbeddedSigning(true);
                draft.setRequesterEmail("jolene@example.com");

                // String clientId = "d7219512693825facdd9241f458decf2";
                // removint this for now, and using the getenv("HS_CLIENT_ID_PROD") instead
                EmbeddedRequest embedReq = new EmbeddedRequest(clientid, draft);

                HelloSignClient client = new HelloSignClient(apikey);
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
                request.setTemplateId("5f15711bf170531c5336528a1a4cbad2bd10da41");
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

                while (true) { //just an always-true statement to keep the while loop running
                    int i = 0;
                    try {
                        System.out.println("Starting loop. Enter ctrl+c to break.\n");
//                        BufferedReader bufferRead1 = new BufferedReader(new InputStreamReader(System.in));
//                        System.out.println(bufferRead1);
//                        String breakOrNo = bufferRead1.readLine();
//                        if (breakOrNo.equals("1")) {
//                            break;
//                        }
//                  commenting all of that out - not sure how to allow user to input but also keep the loop running

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
                        String requestId = newRequest.getId();
                        System.out.print(requestId + " is the signature_request_id\n");
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
                        System.out.print(signID + " is the signature_id of the first signer\n");
                        System.out.print("Embedded Signature Request created! \n");

                        System.out.println("\nWaiting for 10 seconds...\n");
                        TimeUnit.SECONDS.sleep(10);

                        Object filesUrl = client.getFilesUrl(requestId);
                        System.out.println(filesUrl + "\n");

                        SignatureRequest requestGet = client.getSignatureRequest(requestId);
                        System.out.println(requestGet + "\n");

                        EmbeddedResponse embRequest = client.getEmbeddedSignUrl(signID);
                        String signUrl = embRequest.getSignUrl();
                        String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(signUrl, "UTF-8") + "&client_id=" + clientid;
                        System.out.println(url + "\n");

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        System.out.println(dtf.format(now)); //2016/11/16 12:08:43

                        i++;
                        System.out.println(i + " is the number of times this has run");
                        
                        System.out.println("\nWaiting for 5 minutes...\n");
                        TimeUnit.MINUTES.sleep(5);
                        
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
                app.setPrimaryButtonTextColor("#ffffff");
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

                System.out.println(client_id + " is the new client_id\n\n");

                System.out.println("\nEnter 1 if you'd like to delete the new client_id:\n");
                BufferedReader keepAp = new BufferedReader(new InputStreamReader(System.in));
                String keepApOrDelete = keepAp.readLine();
                if (keepApOrDelete.equals("1")) {
                    client.deleteApiApp(client_id);
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

            } else if (options.equals("0")) {
                break;
            } else {
                System.out.println("That was NOT an option - get it together, yo! \n");
            }
        }

    }
}
