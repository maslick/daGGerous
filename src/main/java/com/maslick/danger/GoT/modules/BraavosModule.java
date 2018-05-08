package com.maslick.danger.GoT.modules;

import com.maslick.danger.GoT.Cash;
import com.maslick.danger.GoT.Soldiers;
import dagger.Module;
import dagger.Provides;

@Module
public class BraavosModule {
    Cash cash;
    Soldiers soldiers;


    public BraavosModule(Cash cash, Soldiers soldiers) {
        this.cash = cash;
        this.soldiers = soldiers;
    }

    @Provides
    Cash provideCash() {
        return cash;
    }

    @Provides
    Soldiers provideSoldiers() {
        return soldiers;
    }

}
