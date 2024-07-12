package YDN;
import robocode.*;
import robocode.util.Utils;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class Papi extends AdvancedRobot {

    double enemyBulletHeading;
    double enemyBulletVelocity;
    double enemyBulletX;
    double enemyBulletY;
    Map<String, RobotData> robots = new HashMap<>();

    static class RobotData {
        double x;
        double y;

        public RobotData(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        while (true) {
            setTurnRadarRight(360); // Gira o radar continuamente para detectar inimigos

            // Movimento baseado em vetores para evitar tiros, robôs e paredes
            moveInSafeDirection();

            execute(); // Executa os comandos definidos
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        // Quando um robô inimigo é detectado, vira o canhão e o radar para o inimigo
        double radarTurn = getHeading() - getRadarHeading() + event.getBearing();
        setTurnRadarRightRadians(Utils.normalRelativeAngleDegrees(radarTurn));

        // Quando um robô inimigo atira, guarda as informações da bala
        if (event.getDistance() < 300) { // Suponha que só se preocupa com balas a menos de 300 pixels
            enemyBulletHeading = event.getHeadingRadians();
            enemyBulletVelocity = event.getVelocity();
            enemyBulletX = getX() + event.getDistance() * Math.sin(enemyBulletHeading);
            enemyBulletY = getY() + event.getDistance() * Math.cos(enemyBulletHeading);
        }

        // Atualiza a posição do robô inimigo
        String enemyName = event.getName();
        double enemyX = getX() + event.getDistance() * Math.sin(event.getBearingRadians());
        double enemyY = getY() + event.getDistance() * Math.cos(event.getBearingRadians());
        robots.put(enemyName, new RobotData(enemyX, enemyY));
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        // Remove o robô da lista quando ele é destruído
        robots.remove(event.getName());
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        // Quando é atingido por uma bala, move-se para uma posição segura (em direção oposta à bala)
        double bulletHeading = event.getBullet().getHeadingRadians();
        double bulletVelocity = event.getBullet().getVelocity();

        // Calcula a posição futura da bala inimiga
        double futureBulletX = event.getBullet().getX() + bulletVelocity * Math.sin(bulletHeading);
        double futureBulletY = event.getBullet().getY() + bulletVelocity * Math.cos(bulletHeading);

        // Calcula a direção para se mover (em direção oposta à bala)
        double dx = getX() - futureBulletX;
        double dy = getY() - futureBulletY;
        double angleToMove = Math.atan2(dx, dy);

        setTurnRightRadians(Utils.normalRelativeAngle(angleToMove - getHeadingRadians()));
        setAhead(100);
    }

@Override
public void onHitWall(HitWallEvent event) {
    // Quando atinge a parede, vira para evitar ficar preso nela
    double turnAngle = 90 - Math.abs(getHeading() - event.getBearing());
    if (event.getBearing() < 0) {
        setTurnRight(turnAngle);
    } else {
        setTurnLeft(turnAngle);
    }

    // Adiciona um ajuste para lidar com os cantos
    if (getX() < 50 && getY() < 50) {
        setTurnRight(45);
    } else if (getX() > getBattleFieldWidth() - 50 && getY() < 50) {
        setTurnLeft(45);
    } else if (getX() < 50 && getY() > getBattleFieldHeight() - 50) {
        setTurnLeft(45);
    } else if (getX() > getBattleFieldWidth() - 50 && getY() > getBattleFieldHeight() - 50) {
        setTurnRight(45);
    }

    setAhead(100);
}

    /**
     * Método para mover o robô em uma direção segura, evitando tiros, robôs e paredes.
     */
    private void moveInSafeDirection() {
    double xForce = 0;
    double yForce = 0;

    // Evita tiros inimigos
    xForce += avoidBulletsX();
    yForce += avoidBulletsY();

    // Evita robôs inimigos
    xForce += avoidRobotsX();
    yForce += avoidRobotsY();

    // Evita paredes
    xForce += avoidWallsX();
    yForce += avoidWallsY();

    // Calcula a direção para onde o robô deve se mover
    double angle = Math.atan2(yForce, xForce);
    setTurnRightRadians(Utils.normalRelativeAngle(angle - getHeadingRadians()));
    setAhead(100);
}

private double avoidBulletsX() {
    double xForce = 0;

    // Verifica a posição futura da bala inimiga
    for (Map.Entry<String, RobotData> entry : robots.entrySet()) {
        RobotData robot = entry.getValue();
        double dx = robot.x - getX();
        double dy = robot.y - getY();
        double angle = Math.atan2(dy, dx);

        // Calcula a força com base na distância e velocidade da bala
        double bulletDistance = Point2D.distance(enemyBulletX, enemyBulletY, getX(), getY());
        double force = enemyBulletVelocity / bulletDistance;
        xForce -= force * Math.sin(angle);
    }

    return xForce;
}

private double avoidBulletsY() {
    double yForce = 0;

    // Verifica a posição futura da bala inimiga
    for (Map.Entry<String, RobotData> entry : robots.entrySet()) {
        RobotData robot = entry.getValue();
        double dx = robot.x - getX();
        double dy = robot.y - getY();
        double angle = Math.atan2(dy, dx);

        // Calcula a força com base na distância e velocidade da bala
        double bulletDistance = Point2D.distance(enemyBulletX, enemyBulletY, getX(), getY());
        double force = enemyBulletVelocity / bulletDistance;
        yForce -= force * Math.cos(angle);
    }

    return yForce;
}

private double avoidRobotsX() {
    double xForce = 0;

    for (Map.Entry<String, RobotData> entry : robots.entrySet()) {
        RobotData robot = entry.getValue();
        double dx = robot.x - getX();
        double dy = robot.y - getY();
        double angle = Math.atan2(dy, dx);

        // Calcula a força com base na distância ao robô inimigo
        double distance = Point2D.distance(robot.x, robot.y, getX(), getY());
        xForce += 100 / distance * Math.sin(angle);
    }

    return xForce;
}

private double avoidRobotsY() {
    double yForce = 0;

    for (Map.Entry<String, RobotData> entry : robots.entrySet()) {
        RobotData robot = entry.getValue();
        double dx = robot.x - getX();
        double dy = robot.y - getY();
        double angle = Math.atan2(dy, dx);

        // Calcula a força com base na distância ao robô inimigo
        double distance = Point2D.distance(robot.x, robot.y, getX(), getY());
        yForce += 100 / distance * Math.cos(angle);
    }

    return yForce;
}

private double avoidWallsX() {
    double xForce = 0;

    // Calcula a força para evitar as paredes
    if (getX() <= 50) {
        xForce += 1000 / (getX() - 50); // Increase force as robot approaches wall
    } else if (getX() >= getBattleFieldWidth() - 50) {
        xForce -= 1000 / (getBattleFieldWidth() - getX() - 50); // Increase force as robot approaches wall
    }

    return xForce;
}

private double avoidWallsY() {
    double yForce = 0;

    // Calcula a força para evitar as paredes
    if (getY() <= 50) {
        yForce += 1000 / (getY() - 50); // Increase force as robot approaches wall
    } else if (getY() >= getBattleFieldHeight() - 50) {
        yForce -= 1000 / (getBattleFieldHeight() - getY() - 50); // Increase force as robot approaches wall
    }

    return yForce;
}
}