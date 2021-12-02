package uk.ac.ed.inf;

import org.junit.Test;


import java.time.LocalDate;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class AppTest {

    private static final String VERSION = "1.0.5";
    private static final String RELEASE_DATE = "September 28, 2021";

    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494);
    private final LongLat businessSchool = new LongLat(-3.1873,55.9430);
    private final LongLat greyfriarsKirkyard = new LongLat(-3.1928,55.9469);
//
//    @Test
//    public void testIsConfinedTrueA(){
//        assertTrue(appletonTower.isConfined());
//    }
//
//    @Test
//    public void testIsConfinedTrueB(){
//        assertTrue(businessSchool.isConfined());
//    }
//
//    @Test
//    public void testIsConfinedFalse(){
//        assertFalse(greyfriarsKirkyard.isConfined());
//    }
//
//    private boolean approxEq(double d1, double d2) {
//        return Math.abs(d1 - d2) < 1e-12;
//    }
//
//    @Test
//    public void testDistanceTo(){
//        double calculatedDistance = 0.0015535481968716011;
//        assertTrue(approxEq(appletonTower.distanceTo(businessSchool), calculatedDistance));
//    }
//
//    @Test
//    public void testCloseToTrue(){
//        LongLat alsoAppletonTower = new LongLat(-3.186767933982822, 55.94460006601717);
//        assertTrue(appletonTower.closeTo(alsoAppletonTower));
//    }
//
//
//    @Test
//    public void testCloseToFalse(){
//        assertFalse(appletonTower.closeTo(businessSchool));
//    }
//
//
//    private boolean approxEq(LongLat l1, LongLat l2) {
//        return approxEq(l1.getLongitude(), l2.getLongitude()) &&
//                approxEq(l1.getLatitude(), l2.getLatitude());
//    }
//
//    @Test
//    public void testAngle0(){
//        LongLat nextPosition = appletonTower.nextPosition(0);
//        LongLat calculatedPosition = new LongLat(-3.186724, 55.944494);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle20(){
//        LongLat nextPosition = appletonTower.nextPosition(20);
//        LongLat calculatedPosition = new LongLat(-3.186733046106882, 55.9445453030215);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle50(){
//        LongLat nextPosition = appletonTower.nextPosition(50);
//        LongLat calculatedPosition = new LongLat(-3.186777581858547, 55.94460890666647);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle90(){
//        LongLat nextPosition = appletonTower.nextPosition(90);
//        LongLat calculatedPosition = new LongLat(-3.186874, 55.944644);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle140(){
//        LongLat nextPosition = appletonTower.nextPosition(140);
//        LongLat calculatedPosition = new LongLat(-3.1869889066664676, 55.94459041814145);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle190(){
//        LongLat nextPosition = appletonTower.nextPosition(190);
//        LongLat calculatedPosition = new LongLat(-3.1870217211629517, 55.94446795277335);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle260(){
//        LongLat nextPosition = appletonTower.nextPosition(260);
//        LongLat calculatedPosition = new LongLat(-3.18690004722665, 55.944346278837045);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle300(){
//        LongLat nextPosition = appletonTower.nextPosition(300);
//        LongLat calculatedPosition = new LongLat(-3.186799, 55.94436409618943);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle350(){
//        LongLat nextPosition = appletonTower.nextPosition(350);
//        LongLat calculatedPosition = new LongLat(-3.1867262788370483, 55.94446795277335);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle999(){
//        // The special junk value -999 means "hover and do not change position"
//        LongLat nextPosition = appletonTower.nextPosition(-999);
//        assertTrue(approxEq(nextPosition, appletonTower));
//    }
//
//    @Test
//    public void testMenusOne() {
//        // The webserver must be running on port 9898 to run this test.
//        Menus menus = Menus.getInstance();
//        int totalCost = menus.getDeliveryCost(
//                "Ham and mozzarella Italian roll"
//        );
//        // Don't forget the standard delivery charge of 50p
//        assertEquals(230 + 50, totalCost);
//    }
//
//    @Test
//    public void testMenusTwo() {
//        // The webserver must be running on port 9898 to run this test.
//        Menus menus = Menus.getInstance();
//        int totalCost = menus.getDeliveryCost(
//                "Ham and mozzarella Italian roll",
//                "Salami and Swiss Italian roll"
//        );
//        // Don't forget the standard delivery charge of 50p
//        assertEquals(230 + 230 + 50, totalCost);
//    }
//
//    @Test
//    public void testMenusThree() {
//        // The webserver must be running on port 9898 to run this test.
//        Menus menus = Menus.getInstance();
//        int totalCost = menus.getDeliveryCost(
//                "Ham and mozzarella Italian roll",
//                "Salami and Swiss Italian roll",
//                "Flaming tiger latte"
//        );
//        // Don't forget the standard delivery charge of 50p
//        assertEquals(230 + 230 + 460 + 50, totalCost);
//    }
//
//    @Test
//    public void testMenusFourA() {
//        // The webserver must be running on port 9898 to run this test.
//        Menus menus = Menus.getInstance();
//        int totalCost = menus.getDeliveryCost(
//                "Ham and mozzarella Italian roll",
//                "Salami and Swiss Italian roll",
//                "Flaming tiger latte",
//                "Dirty matcha latte"
//        );
//        // Don't forget the standard delivery charge of 50p
//        assertEquals(230 + 230 + 460 + 460 + 50, totalCost);
//    }
//
//    @Test
//    public void testMenusFourB() {
//        // The webserver must be running on port 9898 to run this test.
//        Menus menus = Menus.getInstance();
//        int totalCost = menus.getDeliveryCost(
//                "Flaming tiger latte",
//                "Dirty matcha latte",
//                "Strawberry matcha latte",
//                "Fresh taro latte"
//        );
//        // Don't forget the standard delivery charge of 50p
//        assertEquals(4 * 460 + 50, totalCost);
//    }

    @Test
    public void d1() {
        var drone = new Drone(businessSchool);
        drone.flyToStop(new Stop("test", appletonTower,"101")); //10moves
        drone.doHover();
        drone.doHover();
//        System.out.println(drone.getFlightPath().size());
        drone.flyToStop(new Stop("test", businessSchool,"101"));
        drone.doHover();
        System.out.println(drone.getFlightPath().size());
        drone.flyToStop(new Stop("test2",appletonTower,"102"));
        drone.doHover();
        System.out.println(drone.getFlightPath().size());
        drone.flyToStop(new Stop("test3",businessSchool,"101"));
        System.out.println(drone.getFlightPath().size());


        System.out.println("\n");
        drone.rollbackOrder();
        System.out.println(drone.getFlightPath().size());
        drone.rollbackOrder();
        System.out.println(drone.getFlightPath().size());
    }

//    @Test
//    public void testRoute1() {
//        LongLat start = new LongLat(-3.1880, 55.9461);
//        LongLat end = new LongLat(-3.1890, 55.9427);
//        Drone drone = new Drone(start);
//        drone.flyToLocation(end);
//        var x = drone.getFlightPath();
//        System.out.println(x.size());
//        System.out.println(DroneMove.getMovesAsFC(x).toJson());
//        assertTrue(x.get(x.size()-1).getTo().closeTo(end));
//    }
//
//
//    @Test
//    public void testRoute2() {
//        PathfindingNode end = new PathfindingNode(-3.191065, 55.945626);
//        PathfindingNode start = new PathfindingNode(-3.186103, 55.944656);
//        var stop = new Stop(null,end,null);
//        Drone drone = new Drone(start);
//        drone.flyToLocation(end);
//        var x = drone.getFlightPath();
//        System.out.println(x.size());
//        assertTrue(x.get(x.size()-1).getTo().closeTo(end));
//        System.out.println(DroneMove.getMovesAsFC(x).toJson()); }
//
//    @Test
//    public void testRoute3() {
//        PathfindingNode start = new PathfindingNode(-3.191065, 55.945626);
//        PathfindingNode end = new PathfindingNode(-3.187837, 55.943497);
//        Drone drone = new Drone(start);
//        drone.flyToLocation(end);
//        var x = drone.getFlightPath();
//        System.out.println(x.size());
//        assertTrue(x.get(x.size()-1).getTo().closeTo(end));
//        System.out.println(DroneMove.getMovesAsFC(x).toJson());
//        System.out.println(start.distanceTo(end)/LongLat.STRAIGHT_LINE_DISTANCE);
//        System.out.println(start.diagonalDistanceTo(end)/LongLat.STRAIGHT_LINE_DISTANCE);
//    }
//
//    @Test
//    public void testRoute4() { //avoid NFZ greggs to cust
//        PathfindingNode start = new PathfindingNode(-3.1887, 55.9459);
//        PathfindingNode end = new PathfindingNode(-3.1893, 55.9434);
//        Drone drone = new Drone(start);
//        drone.flyToLocation(end);
//        var x = drone.getFlightPath();
//        System.out.println(x.size());
//        assertTrue(x.get(x.size()-1).getTo().closeTo(end));
//        System.out.println(DroneMove.getMovesAsFC(x).toJson());
//        System.out.println(start.distanceTo(end)/LongLat.STRAIGHT_LINE_DISTANCE);
//        System.out.println(start.tspHeuristic(end)/LongLat.STRAIGHT_LINE_DISTANCE);
//        System.out.println(start.flightHeuristic(end)/LongLat.STRAIGHT_LINE_DISTANCE);
//    }
//
//    @Test
//    public void testRoute5() {
//        PathfindingNode start = new PathfindingNode(-3.191065, 55.945626);
//        PathfindingNode end = new PathfindingNode(-3.191065, 55.945726);
//        Drone drone = new Drone(start);
//        drone.flyToLocation(end);
//        var x = drone.getFlightPath();
//        System.out.println(x.size());
//        assertTrue(x.get(x.size()-1).getTo().closeTo(end));
//        System.out.println(DroneMove.getMovesAsFC(x).toJson()); }
////
//    @Test
//    public void testRoute6() { //return 1 move.
//        LongLat start = new LongLat(-3.1882, 55.9436);
//        LongLat end = new LongLat(-3.1882, 55.9436+LongLat.STRAIGHT_LINE_DISTANCE+0.00000001);
//        Drone drone = new Drone(start);
//        drone.flyToLocation(end);
//        var x = drone.getFlightPath();
//        System.out.println(x.size());
//        System.out.println(DroneMove.getMovesAsFC(x).toJson());
//        assertEquals(x.size(),1);
//        assertNotEquals(x.get(0).getAngle(),LongLat.JUNK_ANGLE);
//        assertTrue(x.get(x.size()-1).getTo().closeTo(end));
//    }
//
//    @Test
//    public void testRoute7() { //return hover
//        LongLat start = new LongLat(-3.1882, 55.9436);
//        LongLat end = new LongLat(-3.1882, 55.9436+LongLat.STRAIGHT_LINE_DISTANCE-0.00000001);
//        Drone drone = new Drone(start);
//        drone.flyToLocation(end);
//        var x = drone.getFlightPath();
//        System.out.println(x.size());
//        System.out.println(DroneMove.getMovesAsFC(x).toJson());
//        assertEquals(x.size(),1);
//        assertEquals(x.get(0).getAngle(),LongLat.JUNK_ANGLE);
//        assertTrue(x.get(x.size()-1).getTo().closeTo(end));
//    }
//
//    @Test
//    public void testRoute8() { //bottom left cut to top right.
//        LongLat start = new LongLat(-3.1861, 55.9447);
//        LongLat end = new LongLat(-3.1893, 55.9434);
//        Drone drone = new Drone(start);
//        drone.flyToLocation(end);
//        var x = drone.getFlightPath();
//        System.out.println(x.size());
//        System.out.println(DroneMove.getMovesAsFC(x).toJson());
//        System.out.println(start.distanceTo(end) / LongLat.STRAIGHT_LINE_DISTANCE);
//        System.out.println(start.flightHeuristic(end)/LongLat.STRAIGHT_LINE_DISTANCE);
//        assertEquals(x.size(), 23);
//        assertTrue(x.get(x.size()-1).getTo().closeTo(end));
//
//    }
//
//    @Test
//    public void testRoute9() { //Greggs to top right cut
//        LongLat start = new LongLat(-3.1913, 55.9456);
//        LongLat end = new LongLat(-3.1887, 55.9459);
//        Drone drone = new Drone(start);
//        drone.flyToLocation(end);
//        var x = drone.getFlightPath();
//        System.out.println(x.size());
//        System.out.println(DroneMove.getMovesAsFC(x).toJson());
//        System.out.println(start.distanceTo(end) / LongLat.STRAIGHT_LINE_DISTANCE);
//        System.out.println(start.flightHeuristic(end)/LongLat.STRAIGHT_LINE_DISTANCE);
//        assertEquals(x.size(), 17);
//        assertTrue(x.get(x.size()-1).getTo().closeTo(end));
//
//    }
//
//    @Test
//    public void testFetchOrders() {
//        var x = new OrderHandler(31,12,2022);
//        x.fetchOrders();
//    }
//
//    @Test
//    public void getWhichPickups() {
//        var x =  Menus.getInstance();
//        var y = x.getDeliveryShops("Flafel with avocado wrap", "Humus and aubergine wrap",
//                "Goat's cheese salad Italian roll", "Hummus, falafel and spicy tomato French country roll");
//        System.out.println(y.toString());
//    }
//
//    @Test
//    public void getWhichPickups2() {
//        var x =  Menus.getInstance();
//        var y = x.getDeliveryShops("Feta cheese and sundried tomato wrap",
//                "Flafel with humus wrap", "Apple fruit tea", "Mango cap tea");
//        System.out.println(y.toString());
//        var z1 = (new What3Words(y.get(0).location));
//        System.out.println(z1.getCoordinates().toString());
//
//        var z2 = (new What3Words(y.get(1).location));
//        System.out.println(z2.getCoordinates().toString());
//    }
//
//    @Test
//    public void order01() {
//        var x = new Order("c16220b9","s2283092", "surely.native.foal");
//        assertEquals(x.getDestinationW3W(),"surely.native.foal");
//        assertEquals(x.getOrderNo(),"c16220b9");
//        assertEquals(x.getStartCoords(),new What3Words("looks.clouds.daring").getCoordinates());
//    }
//
//    @Test
//    public void testTSP0() {
//        var config = Config.getInstance();
//        var x = new OrderHandler(25,1,2022);
//        x.fetchOrders();
//        var y = new PathBuilder(x);
//        y.buildGraph();
//        y.doTour();
//        FileIO.writeGEOJson(y.getFlightPath(),x.getDate());
//        DatabaseIO.writeDeliveriesTable(y.getOrdersDelivered());
//        DatabaseIO.writeFilepathDatabase(y.getFlightPath());
//
//    }
//
//    @Test
//    public void testTSP() {
//        var config = Config.getInstance();
//        var x = new OrderHandler(6,7,2023);
//        x.fetchOrders();
//        var y = new PathBuilder(x);
//        y.buildGraph();
//        y.doTour();
//        FileIO.writeGEOJson(y.getFlightPath(),x.getDate());
//        DatabaseIO.writeDeliveriesTable(y.getOrdersDelivered());
//        DatabaseIO.writeFilepathDatabase(y.getFlightPath());
//
//    }
//
    @Test
    public void testTSP2() {
        var x = new OrderHandler(27,12,2023);
        x.fetchOrders();
        var y = new PathBuilder(x);
        y.buildGraph();
        y.doTour();
        FileIO.writeGEOJson(y.getFlightPath(),x.getDate());
        DatabaseIO.writeDeliveriesTable(y.getOrdersDelivered());
        DatabaseIO.writeFilepathDatabase(y.getFlightPath());

    }
//
//    @Test
//    public void testTSP3() {
//        var x = new OrderHandler(14,11,2023);
//        x.fetchOrders();
//        var y = new PathBuilder(x);
//        y.buildGraph();
//        y.doTour();
//        FileIO.writeGEOJson(y.getFlightPath(),x.getDate());
//        DatabaseIO.writeDeliveriesTable(y.getOrdersDelivered());
//        DatabaseIO.writeFilepathDatabase(y.getFlightPath());
//
//    }
//
//    @Test
//    public void testTSP4() {
//        var x = new OrderHandler(24,10,2023);
//        x.fetchOrders();
//        var y = new PathBuilder(x);
//        y.buildGraph();
//        y.doTour();
//        FileIO.writeGEOJson(y.getFlightPath(),x.getDate());
//        DatabaseIO.writeDeliveriesTable(y.getOrdersDelivered());
//        DatabaseIO.writeFilepathDatabase(y.getFlightPath());
//
//    }
//
//
//    @Test
//    public void testTSP5() {
//        var x = new OrderHandler(23,12,2023);
//        x.fetchOrders();
//        var y = new PathBuilder(x);
//        y.buildGraph();
//        y.doTour();
//        FileIO.writeGEOJson(y.getFlightPath(),x.getDate());
//        DatabaseIO.writeDeliveriesTable(y.getOrdersDelivered());
//        DatabaseIO.writeFilepathDatabase(y.getFlightPath());
//    }
//
//    @Test
//    public void testA2022Orders() {
//        var start  = LocalDate.of(2022,1,1);
//        var end = LocalDate.of(2023,1,1);
//        var dates = start.datesUntil(end).collect(Collectors.toList());
//
//        var args = new String[5];
//        args[3] = "9898";
//        args[4] = "1527";
//
//        for (LocalDate date : dates) {
//            args[0] = String.valueOf(date.getDayOfMonth());
//            args[1] = String.valueOf(date.getMonthValue());
//            args[2] = String.valueOf(date.getYear());
//            App.main(args);
//        }
//    }
//
    @Test
    public void testAllOrders() {
        var start  = LocalDate.of(2022,1,1);
        var end = LocalDate.of(2024,1,1);
        var dates = start.datesUntil(end).collect(Collectors.toList());

        var args = new String[5];
        args[3] = "9898";
        args[4] = "1527";

        for (LocalDate date : dates) {
            args[0] = String.valueOf(date.getDayOfMonth());
            args[1] = String.valueOf(date.getMonthValue());
            args[2] = String.valueOf(date.getYear());
            App.main(args);
        }
    }
//
//    @Test
//    public void submissionGeoJSONs() {
//        var start  = LocalDate.of(2022,1,1);
//        var end = LocalDate.of(2022,1,13);
//        var dates = start.datesUntil(end).collect(Collectors.toList());
//
//        var args = new String[5];
//        args[3] = "9898";
//        args[4] = "1527";
//
//        for (LocalDate date : dates) { //day matches month
//            args[0] = String.valueOf(date.getDayOfMonth());
//            args[1] = String.valueOf(date.getDayOfMonth());
//            args[2] = String.valueOf(date.getYear());
//            App.main(args);
//        }
//    }

}
