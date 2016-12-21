/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hellosign.apisupport.hellosigntest;

import com.hellosign.sdk.HelloSignClient;

import com.hellosign.sdk.resource.Account;

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

/**
 *
 * @author alexgriffen
 * NOTE TO SELF: you must be in /target to run this
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
                    + "or 0 to exit: ");
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            String things = bufferRead.readLine();

            if (things.equals("1")) {
                // this GETs the account object
                System.out.print(apikey + "\n");
                HelloSignClient client = new HelloSignClient(apikey);
                Account account = client.getAccount();
                System.out.println(account.toString(2));
                
            } else if (things.equals("2")) {
                
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
                request.setSubject("NDA from JAVA");
                request.setTestMode(true);
                request.setMessage("Triggered from Alex's JAVA SDK Integration");
                request.addSigner(emailaddress, signername);
                request.setClientId(clientid);

                //request.addFile(new File("/Users/alexgriffen/NetBeansProjects/HelloSignTest/nda.pdf"));
                request.addFile(new File("/Users/alexgriffen/Downloads/a.pdf"));
                // Prints the JSON response to the console
                SignatureRequest response = client.sendSignatureRequest(request);
                
                System.out.println(response.toString());

                System.out.print("Signature Request sent! \n");
                
            } else if (things.equals("3")) {
                
                //embedded signature request
                SignatureRequest request = new SignatureRequest();
                request.addFile(new File("/Users/alexgriffen/NetBeansProjects/HelloSignTest/nda.pdf"));
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
                String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(signUrl, "UTF-8") + "&client_id=" +clientid;
                System.out.println(url);

                System.out.print("\n");

            } else if (things.equals("4")) {
                
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
                draft.addFile(new File("/Users/alexgriffen/NetBeansProjects/HelloSignTest/nda.pdf"));

                EmbeddedRequest eReq = new EmbeddedRequest(clientid, draft);
                HelloSignClient client = new HelloSignClient(apikey);
                TemplateDraft t = client.createEmbeddedTemplateDraft(eReq);
                String editUrl = t.getEditUrl();
                String templateID = t.getId();
                System.out.print(templateID + "\n");
                String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(editUrl, "UTF-8") + "&client_id=" +clientid;
                System.out.print(url + "\n");

            } else if (things.equals("5")) {
                
                // unclaimed draft with embedded signing
                // aka embedded requesting for embedded signing
                SignatureRequest sigReq = new SignatureRequest();
                sigReq.setTestMode(true);
                sigReq.addFile(new File("/Users/alexgriffen/NetBeansProjects/HelloSignTest/nda.pdf"));

                UnclaimedDraft draft = new UnclaimedDraft(sigReq, UnclaimedDraftType.request_signature);
                draft.setIsForEmbeddedSigning(true);
                draft.setRequesterEmail("jolene@example.com");

                // String clientId = "d7219512693825facdd9241f458decf2";
                // removint this for now, and using the getenv("HS_CLIENT_ID_PROD") instead
                EmbeddedRequest embedReq = new EmbeddedRequest(clientid, draft);

                HelloSignClient client = new HelloSignClient(apikey);
                UnclaimedDraft responseDraft = (UnclaimedDraft) client.createEmbeddedRequest(embedReq);
                String claimUrl = responseDraft.getClaimUrl();
                String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(claimUrl, "UTF-8") + "&client_id=" +clientid;
                System.out.print(url + "\n");

            } else if (things.equals("6")) {
                
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
                String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(claimUrl, "UTF-8") + "&client_id=" +clientid;
                System.out.print(url + "\n");

            } else if (things.equals("7")) {
                
                //embedded requesting with NON embedded siging
                SignatureRequest sigReq = new SignatureRequest();
                sigReq.setTestMode(true);
                sigReq.addFile(new File("/Users/alexgriffen/NetBeansProjects/HelloSignTest/nda.pdf"));

                UnclaimedDraft draft = new UnclaimedDraft(sigReq, UnclaimedDraftType.request_signature);
                draft.setIsForEmbeddedSigning(false);
                draft.setRequesterEmail("alex+javaexample@hellosign.com");

                // String clientId = "d7219512693825facdd9241f458decf2";
                // removint this for now, and using the getenv("HS_CLIENT_ID_PROD") instead
                EmbeddedRequest embedReq = new EmbeddedRequest(clientid, draft);

                HelloSignClient client = new HelloSignClient(apikey);
                UnclaimedDraft responseDraft = (UnclaimedDraft) client.createEmbeddedRequest(embedReq);
                String claimUrl = responseDraft.getClaimUrl();
                String url = "\nhttp://checkembedded.com/?sign_or_template_url=" + URLEncoder.encode(claimUrl, "UTF-8") + "&client_id=" +clientid;
                System.out.print(url + "\n");

            } else if (things.equals("8")) {
                
                // embedded signing with template
                // TODO make this take in a template id and boolean with custom fields
                // SignatureResponse signatureResponse = newSigantureResponse();
                TemplateSignatureRequest request = new TemplateSignatureRequest();
                request.setTemplateId("5f15711bf170531c5336528a1a4cbad2bd10da41");
                request.setSubject("Purchase Order");
                request.setMessage("Glad we could come to an agreement.");
                //String role = "Role1";
                CustomField Cost = new CustomField();
                Cost.setEditor("Role1");
                Cost.setIsRequired(Boolean.TRUE);
                Cost.setValue("$20,000");
                Cost.setName("Cost");
                // CustomField CostAlso = new CustomField();
                // CostAlso.setValue("$30,000");
                // CostAlso.setName("Cost Also");
                request.setSigner("Role1", "george@example.com", "George");
                request.setSigner("Role2", "bob@bobl.com", "Bob");
                request.setSigner("Role3", "frank@frank.com", "Frank");
                request.setSigner("Role4", "bobsson@bob.com", "BobsSon");
                request.setSigner("Role5", "dkslf@dsflk.com", "lksjfl");
                request.addCustomField(Cost);
                // request.addCustomField(CostAlso);
                request.setTestMode(true);
                request.addMetadata("things", "role");
                request.addMetadata("lols", "role");
                

                // String clientId = "d7219512693825facdd9241f458decf2";
                EmbeddedRequest embedReq = new EmbeddedRequest(clientid, request);

                HelloSignClient client = new HelloSignClient(apikey);
                // SignatureRequest newRequest = (SignatureRequest) client.createEmbeddedRequest(embedReq);
                // SignatureRequest newRequest = (SignatureRequest);
                SignatureRequest newRequest = (SignatureRequest) client.createEmbeddedRequest(embedReq);
                String id = newRequest.getId();
                System.out.print(id + "\n");
                System.out.print(newRequest + "\n");

            } else if (things.equals("0")) {
                break;
            } else {
                System.out.println("That was NOT an option - get it together, yo! \n");
            }
        }

    }
}
