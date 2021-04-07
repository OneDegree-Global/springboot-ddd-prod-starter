package hk.onedegree.application.services;

import hk.onedegree.application.aspect.annotations.Authorize;

public class OtherService {

    @Authorize
    public String doWhateverThing(String token){
        System.out.println("hello!");
        return "how do you do?";
    }

    public static void main(String []args) {
        OtherService otherService = new OtherService();
        otherService.doWhateverThing("hio");
    }
}
