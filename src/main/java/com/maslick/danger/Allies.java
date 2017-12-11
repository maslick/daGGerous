package com.maslick.danger;

import javax.inject.Inject;

public class Allies {

    private IronBank ironBank;

    @Inject
    public Allies(IronBank ironBank) {
        this.ironBank = ironBank;
    }

}
