package org.example;

import Server.Server;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.sl.In;
import jdk.internal.org.objectweb.asm.tree.InnerClassNode;
import junit.framework.TestCase;

import java.util.ArrayList;

public class stepDef extends TestCase {
    Server s = new Server(4000, true);
    ArrayList<Integer> dice = new ArrayList<>();
    int fc = 0;
    int score = 0;

    @Given("player roll eight dice {int} {int} {int} {int} {int} {int} {int} {int}")
    public void player_roll_eight_dice(Integer int1, Integer int2, Integer int3, Integer int4, Integer int5, Integer int6, Integer int7, Integer int8) {
        dice.clear();
        dice.add(int1);
        dice.add(int2);
        dice.add(int3);
        dice.add(int4);
        dice.add(int5);
        dice.add(int6);
        dice.add(int7);
        dice.add(int8);
    }
    @Given("fortune card is {int}")
    public void fortune_card_is(Integer int1) {
        fc = int1;
    }
    @When("player end turn")
    public void player_end_turn() {
        score = s.countScore(dice,fc);
    }
    @Then("player score {int}")
    public void player_score(int int1) {
        assertEquals(int1, score);
        s.close();
    }
    @Then("player is {int}")
    public void player_is_dead(int dead) {
        System.out.println(dice);

        if(dead == 1){
            assertTrue(s.isDead(dice, fc));
        }else{
            assertFalse(s.isDead(dice, fc));
        }
    }

    @When("re-roll dice {string} and got new dice {string}")
    public void re_roll_dice_and_got_new_dice(String indexString, String resultString) {
        String[] iString = indexString.split(",");
        ArrayList<Integer> indices = new ArrayList<>();
        for(String index: iString){
            indices.add(0,Integer.parseInt(index));
        }
        ArrayList<Integer> result = new ArrayList<>();
        String[] rString = resultString.split(",");
        for(String index:rString){
            result.add(Integer.parseInt(index));
        }
        s.setReroll(indices);
        dice = s.re_roll(dice, indices,result);
        System.out.println(dice);
    }

}
