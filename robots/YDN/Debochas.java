package YDN;
import java.awt.Color;
import java.awt.geom.Point2D;
import robocode.*;
import robocode.util.Utils;

//Robo baseado no sample Tracker, com ideia de movimentação baseada na estratégia de 
//Ram (travar em um alvo e seguir ele), com implementação básica de previsão de pos-
//ição futura (ainda bem falha se o inimigo está "fugindo" e completamente quebrada
//se o inimigo ficar parado). Escolha de inimigos ainda defeituosa quando em batalhas
//com multíplos inimigos. Salvo a dizer sua velocidade inconstante para inimigos com
//lógicas de previsão semelhantes

public class Debochas extends AdvancedRobot {
	int moveDirection=1;
	
	public void run() {
		setAdjustRadarForRobotTurn(true);//deixa o radar parado durante o gira gira
		setBodyColor(new Color(128, 128, 50));
		setGunColor(new Color(50, 50, 20));
		setRadarColor(new Color(200, 200, 70));
		setScanColor(Color.white);
		setBulletColor(Color.white); //cores
		setAdjustGunForRobotTurn(true); // deixa a arma parada durante o gira gira
		turnRadarRightRadians(Double.POSITIVE_INFINITY);//radar ficar girando pra direita
	}


	public void onScannedRobot(ScannedRobotEvent e) {
		double absBearing=e.getBearingRadians()+getHeadingRadians();//angulo do inimigo
		double latVel=e.getVelocity() * Math.sin(e.getHeadingRadians() -absBearing);//velocidade do inimigo
		double gunTurnAmt;//quanto virar a arma
		double intensidade = 3; 
		setTurnRadarLeftRadians(getRadarTurnRemainingRadians());//trava o radar
		if (e.getDistance() > 900){
	        intensidade = 1;
		} else if (e.getDistance() > 450) {
			intensidade = 2;
		} else {
			intensidade = 3; 
		} 
		if(Math.random()>.9){
			setMaxVelocity((12*Math.random())+12);//varia velocidade nossa
		}
		if (e.getDistance() > 150) {//se tiver longe
			gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/22);//tanto virar arma= angulo inimigo - angulo da arma + velocidade do inimigo/22
			setTurnGunRightRadians(gunTurnAmt); //vira esse tanto
			setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(absBearing-getHeadingRadians()+latVel/getVelocity()));//a ideia é virar pro local provavel do inimigo estar,
																								//tanto pra virar = angulo inimigo - angulo nosso+velocidade inimigo/nossa velocidade
			setAhead((e.getDistance() - 140)*moveDirection);//	vai pra la
			setFire(3);
		}
		else{//se tiver perto
			gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/15);//msm coisa de cima
			setTurnGunRightRadians(gunTurnAmt);
			setTurnLeft(-90-e.getBearing()); //da uma viradinha engana trouxa
			setAhead((e.getDistance() - 140)*moveDirection);
			setFire(intensidade);
		}	
	}

	public void onHitWall(HitWallEvent e){
		moveDirection=-moveDirection;//quando bater na parede, volta pra direção inversa
	}

	public void onWin(WinEvent e) {
		for (int i = 0; i < 50; i++) {
			turnRight(30);
			turnLeft(30);
			ahead(10);
		}
	}
}
