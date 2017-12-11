package com.maslick.danger;

import javax.inject.Inject;

public class Boltons implements House {

    private Allies allies;
    private IronBank ironBank;

    @Inject
    public Boltons(Allies allies, IronBank ironBank) {
        this.allies = allies;
        this.ironBank = ironBank;
    }


    @Override
    public void prepareForWar() {
        //что-то происходит
        System.out.println(this.getClass().getSimpleName() + " prepared for war");
    }
    @Override
    public void reportForWar() {
        //что-то происходит
        System.out.println(this.getClass().getSimpleName() + " reporting..");
    }
}


