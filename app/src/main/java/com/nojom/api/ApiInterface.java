package com.nojom.api;

import com.nojom.model.APIResponse;
import com.nojom.model.APIResponseArray;
import com.nojom.model.AddCard;
import com.nojom.model.CampListResponse;
import com.nojom.model.CampaignUrls;
import com.nojom.model.ChatList;
import com.nojom.model.CommonModel;
import com.nojom.model.MawData;
import com.nojom.model.ServicesData;
import com.nojom.model.UpdateCard;
import com.nojom.model.WalletResponse;
import com.nojom.model.WalletTxnResponse;
import com.nojom.model.WithdrawAmount;
import com.nojom.model.requestmodel.CommonRequest;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;


public interface ApiInterface {

    @Multipart
    @POST
    Call<APIResponse> requestAPI(@Url String url, @Part("data") RequestBody data);

    @Multipart
    @POST
    Call<APIResponse> requestAPIHeader(@Header("sys_id") String sysId, @Header("Authorization") String token, @Url String url, @Part("data") RequestBody data);

    @Multipart
    @POST
    Call<APIResponseArray> simpleRequestAPIHeader(@Header("sys_id") String sysId, @Header("Authorization") String token, @Url String url, @Part("data") RequestBody data);

    @GET
    Call<APIResponse> requestAPIHeader(@Header("Authorization") String token, @Url String url);

    @GET
    Call<APIResponseArray> simpleRequestAPIHeader(@Header("Authorization") String token, @Url String url);

    @GET
    Call<MawData> simpleMawStatus(@Header("Authorization") String token, @Url String url);

    @GET
    Call<ServicesData> getServices(@Header("Authorization") String token, @Url String url);

    @Multipart
    @POST
    Call<APIResponse> requestAPIHeaderFileUpload(@Header("Authorization") String token, @Url String url, @Part("data") RequestBody data, @Part MultipartBody.Part file);

    @Multipart
    @POST
    Call<APIResponse> requestAPIHeaderFileUpload(@Header("Authorization") String token, @Url String url, @Part("data") RequestBody data, @Part MultipartBody.Part file
            , @Part MultipartBody.Part file1);

    @Multipart
    @POST
    Call<APIResponse> requestAPIHeaderFileUpload(@Header("Authorization") String token, @Url String url, @Part("data") RequestBody data, @Part MultipartBody.Part[] file);

    @FormUrlEncoded
    @POST
    Call<APIResponse> requestAPIPOST(@Url String method, @FieldMap HashMap<String, String> defaultData, @Header("Authorization") String token);

    @GET
    Call<APIResponse> requestAPIGET(@Url String method, @Header("Authorization") String token);

    @Multipart
    @POST
    Call<APIResponse> uploadFileBody(@Url String method, @Part MultipartBody.Part[] file,
                                     @Part("gigTitle") RequestBody gigTitle, @Part("parentServiceCategoryID") RequestBody parentServiceCategoryID,
                                     @Part("subServiceCategoryID") RequestBody subServiceCategoryID
            , @Part("description") RequestBody description, @Part("packages") RequestBody packages
            , @Part("searchTags") RequestBody searchTags, @Part("status") RequestBody status, @Part("gigID") RequestBody gigID
            , @Part("languageID") RequestBody languages, @Header("Authorization") String token);

    @Multipart
    @POST
    Call<APIResponse> duplicateGigRequest(@Url String method, @Part MultipartBody.Part[] file,
                                          @Part("gigTitle") RequestBody gigTitle, @Part("parentServiceCategoryID") RequestBody parentServiceCategoryID,
                                          @Part("subServiceCategoryID") RequestBody subServiceCategoryID
            , @Part("description") RequestBody description, @Part("packages") RequestBody packages
            , @Part("searchTags") RequestBody searchTags, @Part("status") RequestBody status, @Part("duplicateGigID") RequestBody gigID
            , @Part("languageID") RequestBody languages, @Part("fileToDelete") RequestBody fileToDelete, @Part("isDuplicate") RequestBody isDuplicate, @Header("Authorization") String token);

    @Multipart
    @POST
    Call<APIResponse> uploadFileBodyCustomGig(@Url String method, @Part MultipartBody.Part[] file,
                                              @Part("gigTitle") RequestBody gigTitle, @Part("parentServiceCategoryID") RequestBody parentServiceCategoryID,
                                              @Part("subServiceCategoryID") RequestBody subServiceCategoryID
            , @Part("description") RequestBody description, @Part("custom_requirments") RequestBody packages, @Part("custom_other_requirments") RequestBody otherReqBody
            , @Part("searchTags") RequestBody searchTags, @Part("status") RequestBody status, @Part("gigID") RequestBody gigID
            , @Part("languageIDs") RequestBody languages, @Part("profile_id") RequestBody profileId, @Part("deadlines") RequestBody deadlineType
            , @Part("minPrice") RequestBody gigPrice, @Header("Authorization") String token, @Part("deadlineDescription") RequestBody deadlineDesc
            , @Part("social_platform") RequestBody platformBody);

    @Multipart
    @POST
    Call<APIResponse> duplicateGigRequestCustomGig(@Url String method, @Part MultipartBody.Part[] file,
                                                   @Part("gigTitle") RequestBody gigTitle, @Part("parentServiceCategoryID") RequestBody parentServiceCategoryID,
                                                   @Part("subServiceCategoryID") RequestBody subServiceCategoryID
            , @Part("description") RequestBody description, @Part("custom_requirments") RequestBody packages, @Part("custom_other_requirments") RequestBody otherReqBody
            , @Part("searchTags") RequestBody searchTags, @Part("status") RequestBody status, @Part("duplicateGigID") RequestBody gigID
            , @Part("languageIDs") RequestBody languages, @Part("fileToDelete") RequestBody fileToDelete,
                                                   @Part("isDuplicate") RequestBody isDuplicate, @Part("profile_id") RequestBody profileId, @Part("deadlines") RequestBody deadlineType
            , @Part("minPrice") RequestBody mainPrice, @Header("Authorization") String token, @Part("deadlineDescription") RequestBody deadlineDesc
            , @Part("social_platform") RequestBody platformBody);


    @Multipart
    @POST
    Call<APIResponse> requestGigList(@Url String method, @PartMap HashMap<String, RequestBody> defaultData, @Header("Authorization") String token);

    @Multipart
    @POST
    Call<APIResponse> requestAPIGIG(@Url String method, @PartMap HashMap<String, RequestBody> defaultData, @Part MultipartBody.Part[] file, @Header("Authorization") String token);

    @Multipart
    @POST
    Call<APIResponse> requestAPIGIG(@Url String method, @PartMap HashMap<String, RequestBody> defaultData, @Part MultipartBody.Part file, @Header("Authorization") String token);


    @FormUrlEncoded
//    @POST(BASE_URL_CHAT + "users/getAllUsersV1")
    @POST
    Call<ChatList> getUser(@Url String method, @Field("profileId") String profileID, @Field("profile_type_id") String profileTypeID);

    @FormUrlEncoded
    @POST
    Call<CommonModel> withdrawOffer(@Url String method, @Field("offerID") String offerID, @Field("PK") String pk, @Field("SK") String sk, @Header("Authorization") String token);

    @GET
    Call<CampListResponse> getCampaign(@Url String method, @Header("Authorization") String token, @Header("sys_id") String sysId);

    @PATCH
    Call<CampListResponse> campStatus(@Url String method, @Header("Authorization") String token, @Header("sys_id") String sysId);

    @Multipart
    @POST
    Call<CampListResponse> uploadCampAttach(@Url String method, @Part MultipartBody.Part[] file, @Header("Authorization") String token);

    @POST
    Call<CampListResponse> uploadCampAttachUrls(@Url String method, @Header("Authorization") String token,
                                                @Body CampaignUrls campaignType, @Header("sys_id") String sysId);

    @GET
    Call<CampListResponse> getCampById(@Url String method, @Header("Authorization") String token, @Header("sys_id") String sysId);

    @GET
    Call<WalletResponse> getWalletBalance(@Url String method, @Header("Authorization") String token, @Header("sys_id") String sysId);

    @GET
    Call<WalletTxnResponse> getWallet(@Url String method, @Header("Authorization") String token, @Header("sys_id") String sysId);

    @GET
    Call<WalletResponse> getAccounts(@Url String method, @Header("Authorization") String token, @Header("sys_id") String sysId);

    @POST
    Call<CommonModel> withdrawAmount(@Url String method, @Header("Authorization") String token,
                                     @Body WithdrawAmount withdrawAmount, @Header("sys_id") String sysId);

    @POST
    Call<CommonModel> addCard(@Url String method, @Header("Authorization") String token,
                              @Body AddCard addCard, @Header("sys_id") String sysId);

    @PUT
    Call<CommonModel> updateCard(@Url String method, @Header("Authorization") String token,
                                 @Body UpdateCard addCard, @Header("sys_id") String sysId);

//    @DELETE
    @HTTP(method = "DELETE", hasBody = true)
    Call<CommonModel> deleteCard(@Url String method, @Header("Authorization") String token,
                                 @Body CommonRequest.DeleteBank addCard, @Header("sys_id") String sysId);

    @GET
    Call<WalletResponse> getHistory(@Url String method, @Header("Authorization") String token, @Header("sys_id") String sysId);
}

