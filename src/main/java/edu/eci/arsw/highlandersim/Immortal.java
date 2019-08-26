package edu.eci.arsw.highlandersim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());
    
    private volatile static boolean isPaused = false;
    
    private static ArrayList<Lock> fightLocks = new ArrayList<Lock>();


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {

        while (true) {
            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);

            this.fight(im);

            try {
                while(isPaused){
                    yield();
                }
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void fight(Immortal i2) {
        int myIndex = immortalsPopulation.indexOf(this);

        int nextFighterIndex = r.nextInt(immortalsPopulation.size());

        //avoid self-fight
        if (nextFighterIndex == myIndex) {
            nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
        }
        while(true){
            if(fightLocks.get(myIndex).tryLock()){
                boolean fightDone = false;
                if(fightLocks.get(nextFighterIndex).tryLock()){
                    fightDone = true;
                    if (i2.getHealth() > 0) {
                        i2.changeHealth(i2.getHealth() - defaultDamageValue);
                        this.health += defaultDamageValue;
        
        
                        updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
                        } else {
                            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
        
                        }
                    fightLocks.get(nextFighterIndex).unlock();
                }
                fightLocks.get(myIndex).unlock();
                if(fightDone)
                    break;
            }
        }
       
    }

    public synchronized void changeHealth(int v) {
        health = v; 
    }

    public synchronized int getHealth() {
        return health;
        
        
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }
    
    public static void pauseOrResume() {
        Immortal.isPaused = !Immortal.isPaused;
    }
    
    public static void setFightLocks(ArrayList<Lock> a) {
        Immortal.fightLocks = a;
    }

}
