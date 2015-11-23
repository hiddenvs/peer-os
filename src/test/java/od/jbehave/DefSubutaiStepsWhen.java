package od.jbehave;

import net.thucydides.core.annotations.Steps;
import od.steps.SubutaiSteps;
import org.jbehave.core.annotations.When;

public class DefSubutaiStepsWhen {

    @Steps
    SubutaiSteps subutaiSteps;

    @When("the user click on the menu item: Environment")
    public void click_on_link_environment(){
        subutaiSteps.clickOnMenuItemEnvironment();
    }

    @When("the user click on the menu item: Blueprint")
    public void click_on_link_blueprint(){
        subutaiSteps.clickOnMenuItemBlueprint();
    }

    @When("the user click on the menu item: Environments")
    public void click_on_link_environments(){
        subutaiSteps.clickOnMenuItemEnvironments();
    }

    @When("the user click on the menu item: Containers")
    public void click_on_link_Containers(){
        subutaiSteps.clickOnMenuItemContainers();
    }

    @When("the user click on the button: Create Blueprint")
    public void click_on_button_create_blueprint(){
        subutaiSteps.clickOnButtonCreateBlueprint();
    }

    @When("the user enter blueprint name: '$blueprintName'")
    public void enter_blueprint_name(String blueprintName){
        subutaiSteps.enterBlueprintName(blueprintName);
    }

    @When("the user enter node name: '$nodeName'")
    public void enter_node_name(String nodeName){
        subutaiSteps.enterNodeName(nodeName);
    }

    @When("the user select template: '$template'")
    public void select_template(String template){
        subutaiSteps.selectTemplate(template);
    }

    @When("the user enter number of containers: '$count'")
    public void enter_number_of_containers(String count){
        subutaiSteps.enterNumberOfContainers(count);
    }

    @When("the user enter SSH group ID: '$id'")
    public void enter_ssh_group_id(String id){
        subutaiSteps.enterSSHGroupID(id);
    }

    @When("the user enter host Group ID: '$id'")
    public void enter_host_group_id(String id){
        subutaiSteps.enterHostGroupID(id);
    }

    @When("the user select quota size: '$quotaSize'")
    public void select_quota_size(String quotaSize){
        subutaiSteps.selectQuotaSize(quotaSize);
    }

    @When("the user click on the button: Add to node list")
    public void click_button_add_to_node_list(){
        subutaiSteps.clickOnButtonAddToNodeList();
    }
}