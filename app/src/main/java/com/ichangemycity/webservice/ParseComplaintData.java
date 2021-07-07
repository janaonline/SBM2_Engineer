package com.ichangemycity.webservice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.appdata.ICMyCPreferenceData;
import com.ichangemycity.model.ComplaintData;
import com.ichangemycity.swachhbharatengineer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.ACTION_VIEW;

/**
 * Created by pattabi.raman on 07-10-2017.
 */

public class ParseComplaintData {
    private static ParseComplaintData mInstance;
    public static ParseComplaintData getInstance() {
        return mInstance == null ? mInstance = new ParseComplaintData() : mInstance;
    }
    public static ArrayList<ComplaintData> getParsedComplaintData(final JSONArray json_comp_array) {

        ArrayList<ComplaintData> data = new ArrayList<ComplaintData>();
        try {
            for(int i = 0; i < json_comp_array.length(); i++) {
                JSONObject json_obj = json_comp_array.getJSONObject(i);
                ComplaintData cData = ComplaintData.getInstance();
                cData.setComplaintId(json_obj.optInt("complaintId") + "");
                cData.setGeneric_id(json_obj.optString("generic_id"));
                cData.setCity_id(json_obj.optInt("city_id") + "");
                cData.setCreated_at(json_obj.optString("created_at"));
                cData.setUser_id(json_obj.optInt("user_id") + "");
                cData.setCategory_id(json_obj.optInt("category_id") + "");
                cData.setVote_up_count(json_obj.optInt("voted_count") + "");
                cData.setComment_count(json_obj.optInt("commented_count") + "");
                cData.setComplaint_url(json_obj.optString("complaint_url"));
                cData.setAffected(json_obj.get("affected").toString());
                cData.setCategory_name(json_obj.optString("category_name"));
                if(json_obj.has("complaint_image"))
                    cData.setComplaint_image(json_obj.optString("complaint_image"));
                else
                    cData.setComplaint_image("http://icmycsaasqa.ichangemycity.com/android/garbage.jpg");

                cData.setComplaint_image_l1(json_obj.optString("complaint_image_l1"));
                cData.setComplaint_image_l1(json_obj.optString("complaint_image_l2"));

                if(json_obj.has("complaint_image_height"))
                    cData.setComplaint_image_height(json_obj.optInt("complaint_image_height") + "");
                else cData.setComplaint_image_height(300 + "");

                cData.setLocation(json_obj.optString("location"));
                cData.setLatitude(json_obj.get("latitude").toString());
                cData.setLongitude(json_obj.get("longitude").toString());

                if(json_obj.has("landmark")) cData.setLandmark(json_obj.optString("landmark"));
                else cData.setLandmark("Landmark missing in web service");
                cData.setParent_id(json_obj.optString("parent_id"));
                cData.setFull_name(json_obj.optString("full_name"));
                if(json_obj.has("user_image"))
                    cData.setUser_image(json_obj.optString("user_image"));
                else
                    cData.setUser_image("http://icmycsaasqa.ichangemycity.com/android/account.png");

                cData.setComplaint_status_id(json_obj.optString("complaint_status_id"));
                cData.setComplaint_status(json_obj.optString("complaint_status"));
                cData.setRadius("" + json_obj.optInt("radius"));
                if(json_obj.has("feed")) {
                    String feed = json_obj.optString("feed");
                    try {
                        cData.setHasFeed(true);
                        JSONObject mComplaintFeedJsonObject = new JSONObject(feed);
                        cData.setFeed_id(mComplaintFeedJsonObject.optString("feed_id"));
                        // cData.setFeed_user_id(mComplaintFeedJsonObject
                        // .optString("feed_user_id"));
                        cData.setFeed_description(mComplaintFeedJsonObject.optString("feed_description"));
                        cData.setFeed_full_name(""/*
                         * mComplaintFeedJsonObject
                         * .getString
                         * ("feed_user_full_name")
                         */);
                        cData.setFeed_color(mComplaintFeedJsonObject.optString("feed_color"));
                        cData.set_is_feed_high_priority(mComplaintFeedJsonObject.get("is_feed_high_priority").toString());
                    } catch(Exception e) {
                        e.printStackTrace();
                        cData.setHasFeed(false);
                    }
                } else {
                    cData.setHasFeed(false);
                }
                if(json_obj.has("feedback_count")) {
                    String feedback_count = json_obj.optString("feedback_count");
                    JSONObject feedback = new JSONObject(feedback_count);
                    cData.setFeedback_count(true);
                    cData.setNeutral(feedback.optInt("neutral") + "");
                    cData.setSatisfaction(feedback.optInt("satisfaction") + "");
                    cData.setUn_satisfied(feedback.optInt("un_satisfied") + "");
                } else {
                    cData.setFeedback_count(false);
                    cData.setNeutral("0");
                    cData.setSatisfaction("0");
                    cData.setUn_satisfied("0");
                }
                // if (!complaintId.contains(json_obj
                // .optString("complaintId"))) {
                data.add(cData);
                // }

                // }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return data;

    }

    public static void setImage(final Activity activity, final CircleImageView circleImageView, final NetworkImageView imageView, final String imageUrl, final boolean isCircularImageView) {
        final ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        if(isCircularImageView) {
            circleImageView.setTag(imageUrl);
            final ImageLoader.ImageContainer container = imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    circleImageView.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    circleImageView.setImageResource(R.mipmap.ic_not_found);
                }
            });
        } else {
            final ImageLoader.ImageContainer container = imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    imageView.setImageUrl(imageUrl, imageLoader);
                    imageView.setOnClickListener(v -> {
                        Intent toPreview = new Intent(ACTION_VIEW, Uri.parse(imageUrl));
                        activity.startActivity(toPreview);
                    });
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    imageView.setImageResource(R.mipmap.ic_not_found);

                }
            });
        }
    }

    public static String getSpanColorForStatusTitle(final int statusId) {
        //        Log.i("getSpanColor", "--------------->" + statusId + "");
        try {
            if(statusId == AppController.COMPLAINT_OPEN || statusId == AppController.COMPLAINT_REOPEN) {
                //                return Color.argb(1, 213, 0, 0);
                return ("#D50000");
            } else if(statusId == AppController.COMPLAINT_ON_THE_JOB) {
                //                return Color.argb(1, 43, 181, 249);
                return ("#2BB5F9");
            } else if(statusId == AppController.COMPLAINT_RESOLVED) {
                //                return Color.argb(0, 189, 0, 1);
                return ("#00BD00");
            } else {
                return ("#607D8B");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return ("#607D8B");
    }

    public static int setBgDrawableForComplaintStatus(final Activity activity, final ComplaintData cData, final TextView complaintStatusTextView) {
        String ComplaintStatusID = cData.getComplaint_status_id();
        int complaintStatusBgDrawable = Integer.parseInt(ComplaintStatusID);
        int complaintStatusTextColor = Color.BLACK;
        switch(complaintStatusBgDrawable) {
            case AppController.COMPLAINT_REOPEN:
                complaintStatusBgDrawable = R.drawable.complaint_status_red;
                complaintStatusTextColor = activity.getResources().getColor(R.color.red_reopn_open);
                break;
            case AppController.COMPLAINT_OPEN:
                complaintStatusBgDrawable = R.drawable.complaint_status_red;
                complaintStatusTextColor = activity.getResources().getColor(R.color.red_reopn_open);
                break;
            case AppController.COMPLAINT_ON_THE_JOB:
                complaintStatusBgDrawable = R.drawable.complaint_status_on_the_job;
                complaintStatusTextColor = activity.getResources().getColor(R.color.blue_on_the_job);
                break;
            case AppController.COMPLAINT_RESOLVED:
                complaintStatusBgDrawable = R.drawable.complaint_status_resolved;
                complaintStatusTextColor = activity.getResources().getColor(R.color.green_resolved);
                break;
            case AppController.COMPLAINT_REJECTED:
                complaintStatusBgDrawable = R.drawable.complaint_status_closed;
                complaintStatusTextColor = activity.getResources().getColor(R.color.gray_closed);
                break;
            default:
                complaintStatusBgDrawable = R.drawable.complaint_status_closed;
                complaintStatusTextColor = activity.getResources().getColor(R.color.gray_closed);
                break;
        }
        complaintStatusTextView.setTextColor(complaintStatusTextColor);
        complaintStatusTextView.setText(cData.getComplaint_status());
        complaintStatusTextView.setBackgroundResource(complaintStatusBgDrawable);
        return complaintStatusBgDrawable;

    }

    public static void shareComplaint(Activity activity, ComplaintData cdata) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.putExtra(Intent.EXTRA_SUBJECT, activity.getResources().getString(R.string.app_name));
            String sAux = ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.user_full_name, "") + " shared a complaint with you.\n\n";
            sAux = sAux + cdata.getComplaint_url();
            if(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.shareImage, "").trim().length() == 0) {
                i.setType("text/plain");
            } else {
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_STREAM, Uri.parse(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.shareImage, "")));
            }
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            activity.startActivity(Intent.createChooser(i, "Share"));
            //            SecurePrefManager.with(activity).set(ICMyCPreferenceData.shareImage).value("").go();
            ICMyCPreferenceData.setPreference(activity, ICMyCPreferenceData.shareImage, "");
        } catch(Exception e) { // e.toString();
        }
    }

    public void setCategoryImage(final Activity activity, final ImageView imageView, final ComplaintData cData) {
        switch(Integer.parseInt(cData.getCategory_id())) {
            case 1:
                Glide.with(activity).load(R.mipmap.cat_dead_animal_1).thumbnail(0.5f).into(imageView);
                break;
            case 2:
                Glide.with(activity).load(R.mipmap.cat_dust_bins_not_cleaned_2).thumbnail(0.5f).into(imageView);
                break;
            case 3:
                Glide.with(activity).load(R.mipmap.cat_garbage_dump_3).thumbnail(0.5f).into(imageView);
                break;
            case 4:
                Glide.with(activity).load(R.mipmap.cat_garbage_vehicle_not_arrived_4).thumbnail(0.5f).into(imageView);
                break;
            case 5:
                Glide.with(activity).load(R.mipmap.cat_sweeping_not_done_5).thumbnail(0.5f).into(imageView);
                break;
            case 6:
                Glide.with(activity).load(R.mipmap.cat_no_electricity_in_toilet_6).thumbnail(0.5f).into(imageView);
                break;
            case 7:
                Glide.with(activity).load(R.mipmap.cat_no_water_supply_in_toilet_7).thumbnail(0.5f).into(imageView);
                break;
            case 8:
                Glide.with(activity).load(R.mipmap.cat_toilet_blockage_8).thumbnail(0.5f).into(imageView);
                break;
            case 9:
                Glide.with(activity).load(R.mipmap.cat_toilet_cleaning_9).thumbnail(0.5f).into(imageView);
                break;
            case 10:
                Glide.with(activity).load(R.mipmap.open_manholes_or_drains).thumbnail(0.5f).into(imageView);
                break;
            case 11:
                Glide.with(activity).load(R.mipmap.sewerage_or_storm_water_overflow).thumbnail(0.5f).into(imageView);
                break;
            case 12:
                Glide.with(activity).load(R.mipmap.stagnant_water_on_the_road).thumbnail(0.5f).into(imageView);
                break;
            case 13:
                Glide.with(activity).load(R.mipmap.improper_disposal_of_fecal_waste_septage).thumbnail(0.5f).into(imageView);
                break;
            case 14:
                Glide.with(activity).load(R.mipmap.debris_removal_construction_material).thumbnail(0.5f).into(imageView);
                break;
            case 15:
                Glide.with(activity).load(R.mipmap.burning_of_garbage_in_open_space).thumbnail(0.5f).into(imageView);
                break;
            case 16:
                Glide.with(activity).load(R.mipmap.urination_in_public_open_defecation).thumbnail(0.5f).into(imageView);
                break;
            case 17:
                // Waste Pickup from Covid-19 Quarantine Area
                //  "http://api.swachh.city/images/categories/waste_pickup.png"
                Glide.with(activity).load("http://api.swachh.city/images/categories/waste_pickup.png").thumbnail(0.5f).into(imageView);
                break;
            case 18:
                // Support for Covid-19 Patient Transport
                //   "http://api.swachh.city/images/categories/support_for_patient_transport.png"
                Glide.with(activity).load("http://api.swachh.city/images/categories/support_for_patient_transport.png").thumbnail(0.5f).into(imageView);
                break;
            case 19:
                // Support for Medicine during Covid-19
                // "http://api.swachh.city/images/categories/support_for_medicine.png"
                Glide.with(activity).load("http://api.swachh.city/images/categories/support_for_medicine.png").thumbnail(0.5f).into(imageView);
                break;
            case 20:
                // Support for Shelter during Covid-19
                // "http://api.swachh.city/images/categories/support_for_shelter.png"
                Glide.with(activity).load("http://api.swachh.city/images/categories/support_for_shelter.png").thumbnail(0.5f).into(imageView);
                break;
            case 21:
                // Support for Food during Covid-19
                // "http://api.swachh.city/images/categories/support_for_food.png"
                Glide.with(activity).load("http://api.swachh.city/images/categories/support_for_food.png").thumbnail(0.5f).into(imageView);
                break;
            case 22:
                // Suspected case of Covid-19 Infection
                // "http://api.swachh.city/images/categories/suspected_case.png"
                Glide.with(activity).load("http://api.swachh.city/images/categories/suspected_case.png").thumbnail(0.5f).into(imageView);
                break;
            case 23:
                // Violation of Lockdown during Covid-19
                // "http://api.swachh.city/images/categories/violation_of_lockdown.png"
                Glide.with(activity).load("http://api.swachh.city/images/categories/violation_of_lockdown.png").thumbnail(0.5f).into(imageView);
                break;
            case 24:
                // Violation of Quarantine during Covid-19
                // "http://api.swachh.city/images/categories/violation_of_quarantine.png"
                Glide.with(activity).load("http://api.swachh.city/images/categories/violation_of_quarantine.png").thumbnail(0.5f).into(imageView);
                break;
            case 25:
                // Request for Fogging/Sanitation during Covid-19
                // "http://api.swachh.city/images/categories/fogging_sanitation.png"
                Glide.with(activity).load("http://api.swachh.city/images/categories/fogging_sanitation.png").thumbnail(0.5f).into(imageView);
                break;
            default:
                //        Glide.with(activity).load(R.mipmap.ic_not_found).thumbnail(0.5f).into(imageView);
        /*imageView.setBackgroundColor(activity.getResources()
            .getColor(AppController.BG_COLOR_DEFAULT[new Random().nextInt(AppController.BG_COLOR_DEFAULT.length-1)]));*/
                imageView.setImageResource(R.mipmap.round_new_releases_white_36);
                imageView.setColorFilter(activity.getResources().getColor(R.color.secondary), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
        }
    }

}
