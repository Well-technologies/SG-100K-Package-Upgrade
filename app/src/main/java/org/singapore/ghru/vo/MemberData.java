package org.singapore.ghru.vo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.singapore.ghru.jobs.SyncHouseholdMemberJob;

import java.util.List;

public class MemberData {
    @Expose
    @SerializedName("data")
    List<SyncHouseholdMemberJob.MemberDTO> data;
    @Expose
    @SerializedName("metadata")
    Metadata metadata;
}


