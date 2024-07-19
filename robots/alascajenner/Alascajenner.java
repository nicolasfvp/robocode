package alascajenner;

import robocode.*;
import java.awt.Color;
import robocode.util.Utils;

public class Alascajenner extends AdvancedRobot {
    
    private double enemyHeading;
    private double enemyVelocity;
    private double enemyDistance;
    
    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        
        while(true) {
            setAhead(100); // Move para frente
            setTurnRightRadians(Math.PI/2); // Vira 90 graus para a direita
            execute(); // Executa as ações pendentes
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double bulletPower = Math.min(2.0, getEnergy());
        double enemyDistance = e.getDistance();
        double enemyX = getX() + enemyDistance * Math.sin(absoluteBearing);
        double enemyY = getY() + enemyDistance * Math.cos(absoluteBearing);
        
        double enemyHeading = e.getHeadingRadians();
        double enemyVelocity = e.getVelocity();
        
        double battlefieldHeight = getBattleFieldHeight();
        double battlefieldWidth = getBattleFieldWidth();
        
        double predictedX = enemyX + Math.sin(enemyHeading) * enemyVelocity;
        double predictedY = enemyY + Math.cos(enemyHeading) * enemyVelocity;
        
        // Limita as coordenadas dentro do campo de batalha
        predictedX = Math.max(18.0, Math.min(predictedX, battlefieldWidth - 18.0));
        predictedY = Math.max(18.0, Math.min(predictedY, battlefieldHeight - 18.0));
        
        double absAngle = Math.atan2(predictedX - getX(), predictedY - getY());
        
        setTurnRightRadians(Math.atan2(predictedX - getX(), predictedY - getY()));
        setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
        setTurnGunRightRadians(Utils.normalRelativeAngle(absAngle - getGunHeadingRadians()));
        fire(bulletPower);
        
        if (enemyVelocity > 0) {
            setAhead(enemyDistance);
        } else {
            setBack(enemyDistance);
        }
    }
    
    public void onHitWall(HitWallEvent e) {
        back(20);
    }
    
    private double normalizeBearing(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }   
}
