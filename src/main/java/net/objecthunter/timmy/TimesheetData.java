package net.objecthunter.timmy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TimesheetData {
	private final String customerName;
	private final String customerStreet;
	private final String customerStreetNumber;
	private final String customerZip;
	private final String customerCity;

	private final String contractorName;
	private final String contractorStreet;
	private final String contractorStreetNumber;
	private final String contractorZip;
	private final String contractorCity;

	private final String projectName;
	private final String projectManager;

	private final List<Timespan> timespans;
	
	private final int sumMinutes;

	private TimesheetData(Builder b) {
		this.customerName = b.customerName;
		this.customerStreet = b.customerStreet;
		this.customerStreetNumber = b.customerStreetNumber;
		this.customerZip = b.customerZip;
		this.customerCity = b.customerCity;
		this.contractorName = b.contractorName;
		this.contractorStreet = b.contractorStreet;
		this.contractorStreetNumber = b.contractorStreetNumber;
		this.contractorZip = b.contractorZip;
		this.contractorCity = b.contractorCity;
		this.projectName = b.projectName;
		this.projectManager = b.projectManager;
		this.timespans = b.timespans;
		this.sumMinutes = b.sumMinutes;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getCustomerStreet() {
		return customerStreet;
	}

	public String getCustomerStreetNumber() {
		return customerStreetNumber;
	}

	public String getCustomerZip() {
		return customerZip;
	}

	public String getCustomerCity() {
		return customerCity;
	}

	public String getContractorName() {
		return contractorName;
	}

	public String getContractorStreet() {
		return contractorStreet;
	}

	public String getContractorStreetNumber() {
		return contractorStreetNumber;
	}

	public String getContractorZip() {
		return contractorZip;
	}

	public String getContractorCity() {
		return contractorCity;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getProjectManager() {
		return projectManager;
	}

	public List<Timespan> getTimespans() {
		return timespans;
	}

	public int getSumMinutes() {
		return sumMinutes;
	}

	public static class Builder {
		private String customerName;
		private String customerStreet;
		private String customerStreetNumber;
		private String customerZip;
		private String customerCity;

		private String contractorName;
		private String contractorStreet;
		private String contractorStreetNumber;
		private String contractorZip;
		private String contractorCity;

		private String projectName;
		private String projectManager;

		private int sumMinutes;

		private List<Timespan> timespans = new ArrayList<Timespan>();

		public Builder customerName(String name) {
			this.customerName = name;
			return this;
		}

		public Builder customerStreet(String name) {
			this.customerStreet = name;
			return this;
		}

		public Builder customerStreetNumber(String number) {
			this.customerStreetNumber = number;
			return this;
		}

		public Builder customerZip(String zip) {
			this.customerZip = zip;
			return this;
		}

		public Builder customerCity(String name) {
			this.customerCity = name;
			return this;
		}

		public Builder contractorName(String name) {
			this.contractorName = name;
			return this;
		}

		public Builder contractorStreet(String name) {
			this.contractorStreet = name;
			return this;
		}

		public Builder contractorStreetNumber(String number) {
			this.contractorStreetNumber = number;
			return this;
		}

		public Builder contractorZip(String zip) {
			this.contractorZip = zip;
			return this;
		}

		public Builder contractorCity(String name) {
			this.contractorCity = name;
			return this;
		}

		public Builder projectName(String name) {
			this.projectName = name;
			return this;
		}

		public Builder projectManager(String manager) {
			this.projectManager = manager;
			return this;
		}

		public Builder timespan(Timespan pacakage) {
			this.timespans.add(pacakage);
			return this;
		}

		public Builder timespans(Collection<Timespan> packages) {
			this.timespans.addAll(packages);
			return this;
		}

		public TimesheetData build() {
			for (Timespan p : timespans) {
				sumMinutes += p.getAmount();
			}
			return new TimesheetData(this);
		}
	}

}
