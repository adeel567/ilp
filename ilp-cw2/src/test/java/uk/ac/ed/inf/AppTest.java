package uk.ac.ed.inf;

import org.junit.Test;

import java.util.PriorityQueue;

import static org.junit.Assert.*;

public class AppTest {

    private static final String VERSION = "1.0.5";
    private static final String RELEASE_DATE = "September 28, 2021";

    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494);
    private final LongLat businessSchool = new LongLat(-3.1873,55.9430);
    private final LongLat greyfriarsKirkyard = new LongLat(-3.1928,55.9469);

    @Test
    public void testIsConfinedTrueA(){
        assertTrue(appletonTower.isConfined());
    }

    @Test
    public void testIsConfinedTrueB(){
        assertTrue(businessSchool.isConfined());
    }

    @Test
    public void testIsConfinedFalse(){
        assertFalse(greyfriarsKirkyard.isConfined());
    }

    private boolean approxEq(double d1, double d2) {
        return Math.abs(d1 - d2) < 1e-12;
    }

    @Test
    public void testDistanceTo(){
        double calculatedDistance = 0.0015535481968716011;
        assertTrue(approxEq(appletonTower.distanceTo(businessSchool), calculatedDistance));
    }

    @Test
    public void testCloseToTrue(){
        LongLat alsoAppletonTower = new LongLat(-3.186767933982822, 55.94460006601717);
        assertTrue(appletonTower.closeTo(alsoAppletonTower));
    }


    @Test
    public void testCloseToFalse(){
        assertFalse(appletonTower.closeTo(businessSchool));
    }


    private boolean approxEq(LongLat l1, LongLat l2) {
        return approxEq(l1.longitude, l2.longitude) &&
                approxEq(l1.latitude, l2.latitude);
    }

    @Test
    public void testAngle0(){
        LongLat nextPosition = appletonTower.nextPosition(0);
        LongLat calculatedPosition = new LongLat(-3.186724, 55.944494);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle20(){
        LongLat nextPosition = appletonTower.nextPosition(20);
        LongLat calculatedPosition = new LongLat(-3.186733046106882, 55.9445453030215);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle50(){
        LongLat nextPosition = appletonTower.nextPosition(50);
        LongLat calculatedPosition = new LongLat(-3.186777581858547, 55.94460890666647);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle90(){
        LongLat nextPosition = appletonTower.nextPosition(90);
        LongLat calculatedPosition = new LongLat(-3.186874, 55.944644);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle140(){
        LongLat nextPosition = appletonTower.nextPosition(140);
        LongLat calculatedPosition = new LongLat(-3.1869889066664676, 55.94459041814145);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle190(){
        LongLat nextPosition = appletonTower.nextPosition(190);
        LongLat calculatedPosition = new LongLat(-3.1870217211629517, 55.94446795277335);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle260(){
        LongLat nextPosition = appletonTower.nextPosition(260);
        LongLat calculatedPosition = new LongLat(-3.18690004722665, 55.944346278837045);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle300(){
        LongLat nextPosition = appletonTower.nextPosition(300);
        LongLat calculatedPosition = new LongLat(-3.186799, 55.94436409618943);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle350(){
        LongLat nextPosition = appletonTower.nextPosition(350);
        LongLat calculatedPosition = new LongLat(-3.1867262788370483, 55.94446795277335);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle999(){
        // The special junk value -999 means "hover and do not change position"
        LongLat nextPosition = appletonTower.nextPosition(-999);
        assertTrue(approxEq(nextPosition, appletonTower));
    }

    @Test
    public void testMenusOne() {
        // The webserver must be running on port 9898 to run this test.
        Menus menus = Menus.getInstance();
        int totalCost = menus.getDeliveryCost(
                "Ham and mozzarella Italian roll"
        );
        // Don't forget the standard delivery charge of 50p
        assertEquals(230 + 50, totalCost);
    }

    @Test
    public void testMenusTwo() {
        // The webserver must be running on port 9898 to run this test.
        Menus menus = Menus.getInstance();
        int totalCost = menus.getDeliveryCost(
                "Ham and mozzarella Italian roll",
                "Salami and Swiss Italian roll"
        );
        // Don't forget the standard delivery charge of 50p
        assertEquals(230 + 230 + 50, totalCost);
    }

    @Test
    public void testMenusThree() {
        // The webserver must be running on port 9898 to run this test.
        Menus menus = Menus.getInstance();
        int totalCost = menus.getDeliveryCost(
                "Ham and mozzarella Italian roll",
                "Salami and Swiss Italian roll",
                "Flaming tiger latte"
        );
        // Don't forget the standard delivery charge of 50p
        assertEquals(230 + 230 + 460 + 50, totalCost);
    }

    @Test
    public void testMenusFourA() {
        // The webserver must be running on port 9898 to run this test.
        Menus menus = Menus.getInstance();
        int totalCost = menus.getDeliveryCost(
                "Ham and mozzarella Italian roll",
                "Salami and Swiss Italian roll",
                "Flaming tiger latte",
                "Dirty matcha latte"
        );
        // Don't forget the standard delivery charge of 50p
        assertEquals(230 + 230 + 460 + 460 + 50, totalCost);
    }

    @Test
    public void testMenusFourB() {
        // The webserver must be running on port 9898 to run this test.
        Menus menus = Menus.getInstance();
        int totalCost = menus.getDeliveryCost(
                "Flaming tiger latte",
                "Dirty matcha latte",
                "Strawberry matcha latte",
                "Fresh taro latte"
        );
        // Don't forget the standard delivery charge of 50p
        assertEquals(4 * 460 + 50, totalCost);
    }


    @Test
    public void testRoute1() {
        LongLat start = new LongLat(-3.1880, 55.9461);
        LongLat end = new LongLat(-3.1890, 55.9427);
        Navigation test = Navigation.getInstance();
        var x = test.getRoute("JUNK",start, end);
        System.out.println(x.size());
        DroneMove.getMovesAsFC(x);
    }

    @Test
    public void testRoute2() {
        aStarNode end = new aStarNode(-3.191065, 55.945626);
        aStarNode start = new aStarNode(-3.186103, 55.944656);
        Navigation test = Navigation.getInstance();
        var x = test.getRoute("JUNK",start, end);
        System.out.println(x.size());
        DroneMove.getMovesAsFC(x);    }

    @Test
    public void testRoute3() {
        aStarNode start = new aStarNode(-3.191065, 55.945626);
        aStarNode end = new aStarNode(-3.187837, 55.943497);
        Navigation test = Navigation.getInstance();
        var x = test.getRoute("JUNK",start, end);
        System.out.println(x.size());
        DroneMove.getMovesAsFC(x);    }

    @Test
    public void testRoute4() {
        aStarNode start = new aStarNode(-3.191594,55.943658);
        aStarNode end = new aStarNode(-3.186199,55.945734);
        Navigation test = Navigation.getInstance();
        var x = test.getRoute("JUNK",start, end);
        System.out.println(x.size());
        DroneMove.getMovesAsFC(x);    }

    @Test
    public void testRoute5() {
        aStarNode start = new aStarNode(-3.191065, 55.945626);
        aStarNode end = new aStarNode(-3.191065, 55.945726);
        Navigation test = Navigation.getInstance();
        var x = test.getRoute("JUNK",start, end);
        System.out.println(x.size());
        DroneMove.getMovesAsFC(x);    }

    @Test
    public void testRoute6() {
        aStarNode start = new aStarNode(-3.191065, 55.945626);
        aStarNode end = new aStarNode(-3.191065, 55.945626+LongLat.STRAIGHT_LINE_DISTANCE+0.000001);
        Navigation test = Navigation.getInstance();
        var x = test.getRoute("JUNK",start, end);
        System.out.println(x.size());
        DroneMove.getMovesAsFC(x);    }

    @Test
    public void testRouteShortHover() {
        LongLat start = new LongLat(-3.1890,55.9452);
        LongLat end = new LongLat(-3.1890+0.00015*2,55.9452);
        Navigation test = Navigation.getInstance();
        var x = test.getRoute("JUNK",start, end);
        x.add(new DroneMove("JUNK",end,end, -999));
        System.out.println(x.size());
        var y = DroneMove.getMovesAsFC(x);

        assertEquals(x.size(),3);

    }

    @Test
    public void testFetchOrders() {
        var x = new OrderHandler(31,12,2022);
        x.fetchOrders();
    }

    @Test
    public void getWhichPickups() {
        var x =  Menus.getInstance();
        var y = x.getDeliveryStops("Flafel with avocado wrap", "Humus and aubergine wrap",
                "Goat's cheese salad Italian roll", "Hummus, falafel and spicy tomato French country roll");
        System.out.println(y.toString());
    }

    @Test
    public void getWhichPickups2() {
        var x =  Menus.getInstance();
        var y = x.getDeliveryStops("Feta cheese and sundried tomato wrap",
                "Flafel with humus wrap", "Apple fruit tea", "Mango cap tea");
        System.out.println(y.toString());
        var z1 = (new What3Words(y.get(0).location));
        System.out.println(z1.getCoordinates().toString());

        var z2 = (new What3Words(y.get(1).location));
        System.out.println(z2.getCoordinates().toString());
    }

    @Test
    public void testTSP0() {
        var config = Config.getInstance();
        var x = new OrderHandler(1,1,2022);
        x.fetchOrders();
        var y = new PathBuilder(x.getAllOrders());
        y.buildGraph();
        y.doTour();
        FileIO.writeGEOJson(y.getFlightPath(),x.getDate());
        DatabaseIO.writeDeliveriesTable(y.getOrdersDelivered());
        DatabaseIO.writeFilepathDatabase(y.getFlightPath());

    }

    @Test
    public void testTSP() {
        var config = Config.getInstance();
        var x = new OrderHandler(29,9,2023);
        x.fetchOrders();
        var y = new PathBuilder(x.getAllOrders());
        y.buildGraph();
        y.doTour();
        FileIO.writeGEOJson(y.getFlightPath(),x.getDate());
        DatabaseIO.writeDeliveriesTable(y.getOrdersDelivered());
        DatabaseIO.writeFilepathDatabase(y.getFlightPath());

    }

    @Test
    public void testTSP2() {
        var x = new OrderHandler(31,12,2023);
        x.fetchOrders();
        var y = new PathBuilder(x.getAllOrders());
        y.buildGraph();
        y.doTour();
        FileIO.writeGEOJson(y.getFlightPath(),x.getDate());
        DatabaseIO.writeDeliveriesTable(y.getOrdersDelivered());
        DatabaseIO.writeFilepathDatabase(y.getFlightPath());

    }

    @Test
    public void testTSP3() {
        var x = new OrderHandler(28,9,2023);
        x.fetchOrders();
        var y = new PathBuilder(x.getAllOrders());
        y.buildGraph();
        y.doTour();
        FileIO.writeGEOJson(y.getFlightPath(),x.getDate());
        DatabaseIO.writeDeliveriesTable(y.getOrdersDelivered());
        DatabaseIO.writeFilepathDatabase(y.getFlightPath());

    }

    @Test
    public void testTSP4() {
        var x = new OrderHandler(31,12,2023);
        x.fetchOrders();
        var y = new PathBuilder(x.getAllOrders());
        y.buildGraph();
        y.doTour();
        FileIO.writeGEOJson(y.getFlightPath(),x.getDate());
        DatabaseIO.writeDeliveriesTable(y.getOrdersDelivered());
        DatabaseIO.writeFilepathDatabase(y.getFlightPath());

    }


    @Test
    public void testTSP5() {
        var x = new OrderHandler(8,11,2023);
        x.fetchOrders();
        var y = new PathBuilder(x.getAllOrders());
        y.buildGraph();
        y.doTour();
        FileIO.writeGEOJson(y.getFlightPath(),x.getDate());
        DatabaseIO.writeDeliveriesTable(y.getOrdersDelivered());
        DatabaseIO.writeFilepathDatabase(y.getFlightPath());
    }

    @Test
    public void testYeet() {
        var x = new aStarNode(0,0);

        var y = new aStarNode(0,0);
        var a = new PriorityQueue<aStarNode>();
        a.add(x);
        System.out.println(a.contains(y));
        a.remove(y);
        a.add(y);
    }
}
