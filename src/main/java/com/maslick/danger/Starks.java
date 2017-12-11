package com.maslick.danger;

import javax.inject.Inject;

public class Starks implements House {


    @Inject
    public Starks() {}

    @Override
    public void prepareForWar() {
        //что-то происходит
        System.out.println(this.getClass().getSimpleName()+" prepared for war");
    }
    @Override
    public void reportForWar() {
        //что-то происходит
        System.out.println(this.getClass().getSimpleName()+" reporting..");
    }
}

