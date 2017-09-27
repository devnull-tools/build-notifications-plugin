package tools.devnull.jenkins.plugins.buildnotifications;
import hudson.Extension;
import hudson.model.Result;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Created by christian on 9/27/17.
 */
@Extension
public class NotifierSettings extends GlobalConfiguration{

    private static String brokenMSG, stillBrokenMSG, fixedMSG, successMSG;
    private static String abortedRES, failureRES, notBuildRES, successRES, unstableRES;

    public NotifierSettings(){
        load();
    }

    @Override
    public String getDisplayName() {
        return "BuildNotifications Settings";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        JSONObject config = json.getJSONObject("base");
        brokenMSG = config.getString("brokenMSG");
        stillBrokenMSG = config.getString("stillBrokenMSG");
        fixedMSG = config.getString("fixedMSG");
        successMSG = config.getString("successMSG");

        abortedRES = config.getString("abortedRES");
        failureRES = config.getString("failureRES");
        notBuildRES = config.getString("notBuildRES");
        successRES = config.getString("successRES");
        unstableRES = config.getString("unstableRES");

        save();
        return true;
    }

    public static String getBrokenMSG() {
        return brokenMSG;
    }

    public static String getStillBrokenMSG() {
        return stillBrokenMSG;
    }

    public static String getFixedMSG() {
        return fixedMSG;
    }

    public static String getSuccessMSG() {
        return successMSG;
    }

    public static String alternativeMSG(BuildStatus bs){
        switch(bs) {
            case BROKEN:
                return getBrokenMSG();
            case STILL_BROKEN:
                return getStillBrokenMSG();
            case FIXED:
                return getFixedMSG();
            case SUCCESSFUL:
                return getSuccessMSG();
            default:
                return "";
        }
    }

    public static String getAbortedRES() {
        return abortedRES;
    }

    public static String getFailureRES() {
        return failureRES;
    }

    public static String getNotBuildRES() {
        return notBuildRES;
    }

    public static String getSuccessRES() {
        return successRES;
    }

    public static String getUnstableRES() {
        return unstableRES;
    }

    public static String alternativeResult(Result res){
        switch(res.toString()){
            case "ABORTED":
                return getAbortedRES();
            case "FAILURE":
                return getFailureRES();
            case "NOT_BUILT":
                return getNotBuildRES();
            case "SUCCESS":
                return getSuccessRES();
            case "UNSTABLE":
                return getUnstableRES();
            default:
                return "";
        }
    }

    /*public String getBrokenMSG(){
        return "Broken ;(";
    }
    public String getStillBrokenMSG(){
        return "Still Broken :|";
    }
    public String getFixedMSG(){
        return "Fixed (y)";
    }
    public String getSuccessMSG(){
        return "Success ;D";
    }*/
}
