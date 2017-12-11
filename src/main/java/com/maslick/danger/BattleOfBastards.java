package com.maslick.danger;

import dagger.Component;

public class BattleOfBastards {

    public static void main(String[] args){
        // Ручное внедрение зависимостей
//        Starks starks = new Starks();
//        Boltons boltons = new Boltons();as
//        war.report();
        BattleComponent component = DaggerBattleComponent.create();
        War war = component.getWar();
        war.prepare();
        war.report();
    }
}

@Component
interface BattleComponent {
    War getWar();
}

