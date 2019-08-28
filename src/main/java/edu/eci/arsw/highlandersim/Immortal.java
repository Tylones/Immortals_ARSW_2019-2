package edu.eci.arsw.highlandersim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private AtomicInteger health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());
    
    private volatile static boolean isPaused = false;

    private boolean thisThreadIsPaused = false;
    
    private static ArrayList<Lock> fightLocks = new ArrayList<Lock>();
    

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = new AtomicInteger(health);
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {

        while (true) {

            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            if(this.getHealth() == 0){
                immortalsPopulation.remove(myIndex);
                this.stop();
            }

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);

            try {
                this.fight(im);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            try {
                while(isPaused){
                    thisThreadIsPaused = true;
                    yield();
                }
                if(thisThreadIsPaused && !isPaused)
                    thisThreadIsPaused = false;
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void fight(Immortal i2) throws InterruptedException {
        int myIndex = immortalsPopulation.indexOf(this);

        int nextFighterIndex = r.nextInt(immortalsPopulation.size());

        //avoid self-fight
        if (nextFighterIndex == myIndex) {
            nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
        }
        while(true){

            // Checkin
            while(isPaused){
                thisThreadIsPaused = true;
                yield();
            }
            if(thisThreadIsPaused && !isPaused)
                    thisThreadIsPaused = false;
                    
            if(fightLocks.get(myIndex).tryLock()){
                boolean fightDone = false;
                if(fightLocks.get(nextFighterIndex).tryLock()){
                    fightDone = true;
                    if (i2.getHealth() > 0) {
                        i2.changeHealth(i2.getHealth() - defaultDamageValue);
                        changeHealth(this.health.get() + defaultDamageValue);
        
        
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

    public void changeHealth(int v) {
        health.set(v);
    }

    public int getHealth() {
        return health.get();
        
        
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }
    
    public static void pause() {
        Immortal.isPaused = true;
    }

    public static void unpause(){
        Immortal.isPaused = false;
    }
    
    public static void setFightLocks(ArrayList<Lock> a) {
        Immortal.fightLocks = a;
    }

    public boolean threadIsPaused(){
        return this.thisThreadIsPaused;
    }

}
