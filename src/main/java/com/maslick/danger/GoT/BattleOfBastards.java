package com.maslick.danger.GoT;

import com.maslick.danger.DaggerBattleComponent;
import com.maslick.danger.GoT.modules.BraavosModule;
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

        Cash cash1 = component.getCash();
        Soldiers soldiers1 = component.getSoldiers();
    }
}

@Component(modules = BraavosModule.class)
interface BattleComponent {
    War getWar();
    Cash getCash();
    Soldiers getSoldiers();
}

