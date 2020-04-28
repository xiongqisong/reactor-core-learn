package com.xqs.reactor_core_learn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws CloneNotSupportedException {
        // test3();
    }

    public static void test1() {
        HashMap<Integer, User> map = new HashMap<>();
        map.put(1, new User("xqs"));
        User u1 = map.get(1);
        HashMap<Integer, User> cloneMap = new HashMap<>();
        // cloneMap.putAll(map);
        cloneMap = (HashMap<Integer, User>) map.clone();
        User u2 = cloneMap.get(1);
        System.out.println((map == cloneMap) + "  " + map.size() + "  " + cloneMap.size());
        System.out.println(u1 == u2);
        System.out.println(u1.show() + "  " + u2.show());
        u2.setName("zy");
        cloneMap.put(2, new User("jj"));
        System.out.println((map == cloneMap) + "  " + map.size() + "  " + cloneMap.size());
        System.out.println(u1 == u2);
        System.out.println(u1.show() + "  " + u2.show());
    }

    public static void test2() throws CloneNotSupportedException {
        User u1 = new User("xqs");
        User u2 = (User) u1.clone();
        System.out.println((u1 == u2) + "  " + (u1.getName() == u2.getName()));
        System.out.println(u1.getName() + "  " + u2.getName());
        u2.setName("zy");
        System.out.println((u1 == u2) + "  " + (u1.getName() == u2.getName()));
        System.out.println(u1.getName() + "  " + u2.getName());

        String a1 = new String("abc");
        String a2 = new String("abc");
        System.out.println(a1 == a2);
    }

    public static void test3() throws CloneNotSupportedException {
        Members m1 = new Members(new HashMap<>());
        m1.put(1, new User("xqs"));
        Members m2 = (Members) m1.clone();
        System.out.println(m1 == m2);
        User u1 = m1.get(1);
        User u2 = m2.get(1);
        System.out.println((u1 == u2) + "  " + (u1.getName() == u2.getName()) + "  " + u1.show() + "  " + u2.show());
        u2.setName("zy");
        System.out.println((u1 == u2) + "  " + (u1.getName() == u2.getName()) + "  " + u1.show() + "  " + u2.show());

        m2.put(2, new User("jj"));
        System.out.println(m1.size() + "  " + m2.size());
    }

    public static void test4() {
        HashMap<String, String> data = new HashMap<>();
        data.put("1", "1");

        HashMap<String, String> can = (HashMap<String, String>) getMap(String.class, data);
        can.put("2", "2");
        System.out.println(can);

        Map<String, String> un = getMap(Integer.class, data);
        System.out.println(un);
    }

    public static Map<String, String> getMap(Class<?> clazz, HashMap<String, String> map) {
        if (clazz.equals(String.class)) {
            return map;
        } else {
            return Collections.unmodifiableMap(map);
        }
    }
}

class Members implements Cloneable {
    HashMap<Integer, User> table;

    public Members(HashMap<Integer, User> table) {
        super();
        this.table = table;
    }

    public HashMap<Integer, User> getTable() {
        return table;
    }

    public void setTable(HashMap<Integer, User> table) {
        this.table = table;
    }

    public void put(Integer key, User value) {
        table.put(key, value);
    }

    public User get(Integer key) {
        return table.get(key);
    }

    public int size() {
        return table.size();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        HashMap<Integer, User> cloneTable = new HashMap<>();
        table.entrySet().stream().forEach(entry -> {
            try {
                cloneTable.put(entry.getKey(), (User) entry.getValue().clone());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return new Members(cloneTable);
    }

}

class User implements Cloneable {
    private String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String show() {
        return "User [name=" + name + "]";
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new User(new String(name));
    }

}
