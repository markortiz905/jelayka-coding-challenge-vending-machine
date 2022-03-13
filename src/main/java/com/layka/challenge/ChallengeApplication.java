package com.layka.challenge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChallengeApplication {

	static ObjectMapper mapper = new ObjectMapper();
	static Map<String, Object> config;
	static Map<String, Object> selections;

	public static void main(String[] args) throws IOException {
		String inputJson = new String(Files.readAllBytes(Paths.get("input.json")));
		
		config = mapper.readValue(inputJson, new TypeReference<Map<String, Object>>(){});

		createMenu();
		displayMenu();
		Scanner input = new Scanner(System.in);
		while (true) {
			System.out.print("Enter selection: ");
			String selection = input.nextLine();
			Map<String, Object> selected = getSelection(selection);
			System.out.println("You Selected: " + selected.get("name"));
			System.out.println("Price: " + selected.get("price"));
			System.out.println("Stock: " + selected.get("amount"));

			int stock = (int)selected.get("amount");
			if (stock == 0) {
				System.out.println("no available stock... please enter to try again or x to exit program");
				String exit = input.nextLine();
				if (exit.equals("x")) {
					break;
				}
			} else {
				System.out.println("In Stock. Please Insert Coin");
				double coins = Double.parseDouble(input.nextLine());
				System.out.println("You inserted " + coins + " coin(s)");
				if (validatePayment(coins, selected)) {
					System.out.println("Processing.....");
					double change = updateItem(selected, coins);
					System.out.println("Here's your change ... $" + change);
					System.out.println("Heres your item...... " + selected.get("name"));
					System.out.println("Thanks... press enter for next order");
					input.nextLine();
				} else {
					System.out.println("Not enough, returning coins and try again..\n");
				}
			}
		}
		input.close();
	}

	private static double updateItem(Map<String, Object> selected, double coins) {
		double price = Double.parseDouble(selected.get("price").toString().replace("$", ""));
		double change = coins - price;
		selected.put("amount", ((int)selected.get("amount"))-1 );
		return change;
	}

	private static boolean validatePayment(double coins, Map<String, Object> selected) {
		double price = Double.parseDouble(selected.get("price").toString().replace("$", ""));
		if (coins < price) {
			System.out.println("Coin Not Enough... Please try again");
			return false;
		} else {
			return true;
		}

	}

	private static void createMenu() {

		selections = new HashMap<>();

		Map<String, Object> menuConfig = (Map<String, Object>)config.get("config");
		// columns
		int numCols = Integer.parseInt((String)menuConfig.get("columns"));
		//rows
		int numRows = (int)menuConfig.get("rows");
		String[] letters = new String[numRows];
		//items
		List<Map<String, Object>> items = (List<Map<String, Object>>)config.get("items");
		
		int chr = 65;
		int itemIndex=0;
		for (int row=0;row<numRows;row++) {
			for (int col=0;col<numCols && itemIndex<items.size();col++) {
				Map<String, Object> item = items.get(itemIndex);
				String selector = ((char)chr) + "" + col;
				System.out.println(selector);
				selections.put(selector, item);
				itemIndex++;
			}
			chr++;
		}
	}

	private static Map<String, Object> getSelection(String selection) {
		for(Map.Entry<String, Object> entry:selections.entrySet()) {
			if(entry.getKey().equals(selection)) {
				return (Map<String, Object>)entry.getValue();
			}
		}
		return null;
	}

	private static void displayMenu() {
		Map<String, Object> menuConfig = (Map<String, Object>)config.get("config");
		// columns
		int numCols = Integer.parseInt((String)menuConfig.get("columns"));
		//rows
		int numRows = (int)menuConfig.get("rows");
		String[] letters = new String[numRows];
		//items
		List<Map<String, Object>> items = (List<Map<String, Object>>)config.get("items");
		
		int chr = 65;

		StringBuilder builder = new StringBuilder();
		builder.append("\n");
		for (int col=0;col<numCols;col++) {
			builder.append("|\t   " + col);
		}

		int itemIndex=0;
		builder.append("\n");
		for (int x=0;x<letters.length;x++) {
			letters[x] = "" + (char)chr;
			builder.append(letters[x]);

			//print items
			for (int col=0;col<numCols && itemIndex<items.size();col++) {
				Map<String, Object> item = items.get(itemIndex);
				String itemName = (String)item.get("name");
				builder.append("|\t" + itemName);
				itemIndex++;
			}

			builder.append("\n");
			chr++;
		}

		System.out.println("---------MENU------------");
		System.out.println(builder.toString());
	}
}
