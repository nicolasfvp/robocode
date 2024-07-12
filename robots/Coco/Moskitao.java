package Coco;
import robocode.*;
import java.awt.Color;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class Moskitao extends AdvancedRobot

    @Override
    public void run() {
        while (true) {
            // Move-se aleatoriamente para evitar ficar parado
            setAhead(100);
            setTurnRight(90);

            // Espera um pouco antes de realizar a próxima ação
            execute();
            waitFor(new TurnCompleteCondition(this));
        }
    }

    @Override
    public void onScannedBullet(ScannedBulletEvent event) {
        // Acessa a bala inimiga detectada
        Bullet bullet = event.getBullet();

        // Obtém informações da bala inimiga
        double enemyBulletHeading = bullet.getHeading();
        double enemyBulletVelocity = bullet.getVelocity();
        double enemyBulletX = bullet.getX();
        double enemyBulletY = bullet.getY();

        // Calcula a distância entre o robô e a bala inimiga
        double dx = enemyBulletX - getX();
        double dy = enemyBulletY - getY();
        double distanceToBullet = Math.sqrt(dx * dx + dy * dy);

        // Calcula o tempo até o impacto da bala
        double timeToImpact = distanceToBullet / enemyBulletVelocity;

        // Calcula a posição futura da bala
        double futureBulletX = enemyBulletX + enemyBulletVelocity * timeToImpact * Math.sin(Math.toRadians(enemyBulletHeading));
        double futureBulletY = enemyBulletY + enemyBulletVelocity * timeToImpact * Math.cos(Math.toRadians(enemyBulletHeading));

        // Calcula a posição segura para se mover (move-se na direção oposta)
        double safeX = getX() + (getX() - futureBulletX);
        double safeY = getY() + (getY() - futureBulletY);

        // Move o robô para a posição segura
        goTo(safeX, safeY);
    }

    public void goTo(double x, double y) {
        // Calcula a distância e o ângulo para a posição x, y
        double dx = x - getX();
        double dy = y - getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double angle = Utils.normalRelativeAngleDegrees(Math.toDegrees(Math.atan2(dx, dy)) - getHeading());

        // Vira o robô para o ângulo desejado
        setTurnRight(angle);

        // Move o robô para a posição
        setAhead(distance);
    }
}