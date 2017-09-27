package tools.devnull.jenkins.plugins.buildnotifications;
import hudson.Extension;
import hudson.XmlFile;
import hudson.model.Result;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Created by christian on 9/27/17.
 */
@Extension
public class NotifierSettings extends GlobalConfiguration{

    private String brokenMSG, stillBrokenMSG, fixedMSG, successMSG;
    private String abortedRES, failureRES, notBuildRES, successRES, unstableRES;

    public NotifierSettings(){
        load();
    }

    @DataBoundConstructor
    public NotifierSettings(String brokenMSG, String stillBrokenMSG, String fixedMSG, String successMSG,
                            String abortedRES, String failureRES, String notBuildRES, String successRES, String unstableRES){
        this.brokenMSG = brokenMSG;
        this.stillBrokenMSG = stillBrokenMSG;
        this.fixedMSG = fixedMSG;
        this.successMSG = successMSG;

        this.abortedRES = abortedRES;
        this.failureRES = failureRES;
        this.notBuildRES = notBuildRES;
        this.successRES = successRES;
        this.unstableRES = unstableRES;
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

    public String getBrokenMSG() {
        return brokenMSG;
    }

    public String getStillBrokenMSG() {
        return stillBrokenMSG;
    }

    public String getFixedMSG() {
        return fixedMSG;
    }

    public String getSuccessMSG() {
        return successMSG;
    }

    /*public void setBrokenMSG(String brokenMSG) {
        this.brokenMSG = brokenMSG;
    }

    public void setStillBrokenMSG(String stillBrokenMSG) {
        this.stillBrokenMSG = stillBrokenMSG;
    }

    public void setFixedMSG(String fixedMSG) {
        this.fixedMSG = fixedMSG;
    }

    public void setSuccessMSG(String successMSG) {
        this.successMSG = successMSG;
    }*/

    public String alternativeMSG(BuildStatus bs){
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

    public String getAbortedRES() {
        return abortedRES;
    }

    public String getFailureRES() {
        return failureRES;
    }

    public String getNotBuildRES() {
        return notBuildRES;
    }

    public String getSuccessRES() {
        return successRES;
    }

    public String getUnstableRES() {
        return unstableRES;
    }

    /*public void setAbortedRES(String abortedRES) {
        this.abortedRES = abortedRES;
    }

    public void setFailureRES(String failureRES) {
        this.failureRES = failureRES;
    }

    public void setNotBuildRES(String notBuildRES) {
        this.notBuildRES = notBuildRES;
    }

    public void setSuccessRES(String successRES) {
        this.successRES = successRES;
    }

    public void setUnstableRES(String unstableRES) {
        this.unstableRES = unstableRES;
    }*/

    public String alternativeResult(Result res){
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
