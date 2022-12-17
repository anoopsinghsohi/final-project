package com.prog.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.prog.entites.AccountBalance;
import com.prog.entites.AccountTransaction;
import com.prog.entites.UserDtls;
import com.prog.repository.UserRepository;
import com.prog.service.AdminService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home() {
		return "admin/home";
	}

	@GetMapping("/accStatus")
	public String accStatus(Model m) {
		m.addAttribute("accSt", adminService.getAllUserByStatus("ROLE_USER", "false"));
		return "admin/acc_status";
	}

	@GetMapping("/allTrans")
	public String allTrans(Model m) {
		m.addAttribute("trans", adminService.getAllTrans());
		return "admin/all_transaction";
	}

	@GetMapping("/trans")
	public String trans(Model m) {
		m.addAttribute("st", "no");
		return "admin/transaction";
	}

	@PostMapping("/searchAcct")
	public String searchAccount(@RequestParam("accNum") String accNum, Model m) {
		m.addAttribute("st", "yes");
		m.addAttribute("acct", adminService.getDetailsByAcccountNum(accNum.trim()));

		return "admin/transaction";
	}

	@GetMapping("/account")
	public String account(Model m) {
		m.addAttribute("accSt", adminService.getAllUserByStatus("ROLE_USER", "true"));
		return "admin/all_account";
	}

	@GetMapping("/viewAccount/{id}")
	public String viewAccount(Model m, @PathVariable int id) {
		m.addAttribute("user", userRepo.findById(id).get());
		return "admin/view_account";
	}

	@GetMapping("/AcctSt/{id}/{st}")
	public String accountStatus(@PathVariable int id, @PathVariable String st, HttpSession session) {

		UserDtls user = userRepo.findById(id).get();
		Random rd = new Random();
		if ("ap".equals(st)) {
			user.setAccStatus("true");
			user.setAccountNum("102034" + rd.nextInt(1000));
			userRepo.save(user);

			AccountTransaction trans = new AccountTransaction();
			trans.setUser(user);

			AccountBalance bal = new AccountBalance();
			bal.setUser(user);
			bal.setTotalBalance(500.00);

			adminService.saveTrans(trans);
			adminService.saveBalance(bal);
			session.setAttribute("msg", "Account Created Sucessfully");
		} else {
			userRepo.delete(user);
			session.setAttribute("msg", "Account Rejected");
		}

		return "redirect:/admin/accStatus";
	}

	@PostMapping("/saveTrans")
	public String saveTransaction(@RequestParam String transType, @RequestParam String amt, @RequestParam int id,
			Model m, HttpSession session, @RequestParam String tbalance, @RequestParam String accno) {

		Double amtx = Double.parseDouble(amt);
		Double tamtx = Double.parseDouble(tbalance);

		if ("Debit".equals(transType)) {
			if (amtx > tamtx) {
				session.setAttribute("msg", "insufficent Balance");
			} else {
				adminService.saveTrans(transType, amtx, id);
				session.setAttribute("sucMsg", "Transaction success");
			}

		} else if ("Credit".equals(transType)) {
			adminService.saveTrans(transType, amtx, id);
			session.setAttribute("sucMsg", "Transaction success");
		} else {
			session.setAttribute("msg", "Choose Transaction Type");
		}
		return "redirect:/admin/trans";
	}

}
