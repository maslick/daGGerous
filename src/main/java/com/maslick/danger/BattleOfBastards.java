package com.maslick.danger;

import com.maslick.danger.modules.BraavosModule;
import dagger.Component;

public class BattleOfBastards {

    public static void main(String[] args){
        // Ручное внедрение зависимостей
        // Starks starks = new Starks();
        // Boltons boltons = new Boltons();
        // War war = new War(starks,boltons);
        // war.prepare();
        // war.report();

        Cash cash = new Cash();
        Soldiers soldiers = new Soldiers();

        BattleComponent component = DaggerBattleComponent
                .builder()
                .braavosModule(new BraavosModule(cash, soldiers))
                .build();
        War war = component.getWar();
        war.prepare();
        war.report();

        component.getCash();
        component.getSoldiers();
    }
}

@Component(modules = BraavosModule.class)
interface BattleComponent {
    War getWar();
    Cash getCash();
    Soldiers getSoldiers();
}

