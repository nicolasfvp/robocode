package Esquivador;

import robocode.*;
import java.awt.Color;
import robocode.util.Utils;

public class EsquivoEsquivo extends AdvancedRobot {
    boolean movingForward = true;

    @Override
    public void run() {
        setColors(Color.red, Color.blue, Color.green); // Corpo, arma, radar
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        // Loop principal
        while (true) {
            // Gira o radar para encontrar o inimigo
            setTurnRadarRight(Double.POSITIVE_INFINITY);
            
            // Movimentação em serpente
            serpentMovement();

            // Executa os comandos pendentes
            execute();
        }
    }

    private void serpentMovement() {
        // Calcula as coordenadas da próxima posição
        double nextX = getX() + (movingForward ? 100 : -100) * Math.sin(Math.toRadians(getHeading()));
        double nextY = getY() + (movingForward ? 100 : -100) * Math.cos(Math.toRadians(getHeading()));

        // Verifica se a próxima posição está fora dos limites do campo de batalha
        boolean tooCloseToWall = nextX < 50 || nextY < 50 || nextX > getBattleFieldWidth() - 50 || nextY > getBattleFieldHeight() - 50;

        // Se estiver muito próximo da parede, ajusta a direção
        if (tooCloseToWall) {
            setTurnRight(90);
        } else {
            // Movimento em serpente padrão
            if (movingForward) {
                setAhead(100);
            } else {
                setBack(100);
            }
            setTurnRight(10); // Ajuste para virar levemente à direita após cada movimento
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        // Mirar no inimigo
        double absoluteBearing = getHeading() + event.getBearing();
        double bearingFromGun = Utils.normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

        // Gira a arma para o inimigo
        setTurnGunRight(bearingFromGun);
        
        // Dispara quando a arma estiver alinhada com o inimigo
        if (getGunHeat() == 0 && Math.abs(bearingFromGun) <= 3) {
            setFire(Math.min(400 / event.getDistance(), 3));
        }

        // Redefinir o radar para travar no alvo
        setTurnRadarRight(getRadarTurnRemaining());
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        // Reverter direção quando bater na parede
        if (movingForward) {
            setBack(100);
            movingForward = false;
        } else {
            setAhead(100);
            movingForward = true;
        }
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        // Reverter direção ao colidir com outro robô
        if (event.isMyFault()) {
            if (movingForward) {
                setBack(100);
                movingForward = false;
            } else {
                setAhead(100);
                movingForward = true;
            }
        }
    }
}