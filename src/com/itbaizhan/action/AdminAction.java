package com.itbaizhan.action;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.itbaizhan.entity.Admin;
import com.itbaizhan.entity.Book;
import com.itbaizhan.entity.Category;
import com.itbaizhan.entity.Indent;
import com.itbaizhan.entity.Items;
import com.itbaizhan.entity.Users;
import com.itbaizhan.service.AdminService;
import com.itbaizhan.service.BookService;
import com.itbaizhan.service.CategoryService;
import com.itbaizhan.service.IndentService;
import com.itbaizhan.service.UserService;
import com.itbaizhan.util.PageUtil;
import com.itbaizhan.util.SafeUtil;
import com.itbaizhan.util.UploadUtil;

@Namespace("/admin")
@Action("admin")
@Results({
	@Result(name="login",location="/admin/login.jsp"),
	@Result(name="main",location="/admin/main.jsp"),
	@Result(name="indent",location="/admin/pages/indent-list.jsp"),
	@Result(name="reindent",type="redirect",location="admin!indentList.action?status=${status}&page=${page}"),
	@Result(name="item",location="/admin/pages/item-list.jsp"),
	@Result(name="user",location="/admin/pages/user-list.jsp"),
	@Result(name="useradd",location="/admin/pages/user-add.jsp"),
	@Result(name="userreset",location="/admin/pages/user-reset.jsp"),
	@Result(name="userupdate",location="/admin/pages/user-update.jsp"),
	@Result(name="reuser",type="redirect",location="admin!userList.action?page=${page}"),
	@Result(name="book",location="/admin/pages/book-list.jsp"),
	@Result(name="bookadd",location="/admin/pages/book-add.jsp"),
	@Result(name="bookupdate",location="/admin/pages/book-update.jsp"),
	@Result(name="rebook",type="redirect",location="admin!bookList.action?status=${status}&page=${page}"),
	@Result(name="category",location="/admin/pages/category-list.jsp"),
	@Result(name="categoryupdate",location="/admin/pages/category-update.jsp"),
	@Result(name="recategory",type="redirect",location="admin!categoryList.action?page=${page}"),
	@Result(name="admin",location="/admin/pages/admin-list.jsp"),
	@Result(name="adminadd",location="/admin/pages/admin-add.jsp"),
	@Result(name="adminreset",location="/admin/pages/admin-reset.jsp"),
	@Result(name="readmin",type="redirect",location="admin!adminList.action?page=${page}"),
})	
public class AdminAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	private static final int rows = 10;
	
	@Resource
	private AdminService adminService;
	@Resource
	private IndentService indentService;
	@Resource
	private UserService userService;
	@Resource
	private BookService bookService;
	@Resource
	private CategoryService categoryService;
	
	private List<Indent> indentList;
	private List<Items> itemList;
	private List<Users> userList;
	private List<Book> bookList;
	private List<Category> categoryList;
	private List<Admin> adminList;
	
	private Users user;
	private Book book;
	private Category category;
	private Admin admin;
	private int status;
	private int flag;
	private int id;
	
	private File photo;		//??????????????????
    private String photoFileName;	//????????????????????????
    private String photoContentType;		//????????????????????????
	
    
	/**
	 * ???????????????
	 * @return
	 */
	public String login() {
		if (adminService.checkUser(admin.getUsername(), admin.getPassword())) {
			getSession().put("admin", admin.getUsername());
			return "main";
		}
		addActionError("????????????????????????!");
		return "login";
	}
	
	
	/**
	 * ????????????
	 * @return
	 */
	public String indentList(){
		indentList = indentService.getList(status, page, rows);
		pageTool = PageUtil.getPageToolAdmin(servletRequest, indentService.getTotal(status), page, rows);
		return "indent";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String indentDispose(){
		indentService.dispose(id);
		return "reindent";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String indentDelete(){
		indentService.delete(id);
		return "reindent";
	}
	
	/**
	 * ???????????????
	 * @return
	 */
	public String itemList(){
		itemList = indentService.getItemList(id, page, rows);
		pageTool = PageUtil.getPageToolAdmin(servletRequest, indentService.getItemTotal(id), page, rows);
		return "item";
	}
	
	
	/**
	 * ????????????
	 * @return
	 */
	public String userList(){
		userList = userService.getList(page, rows);
		pageTool = PageUtil.getPageToolAdmin(servletRequest, userService.getTotal(), page, rows);
		return "user";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String userAdd(){
		if (userService.isExist(user.getUsername())) {
			addActionError("??????????????????!");
			return "useradd";
		}
		 userService.add(user);
		 return "reuser";
	}
	
	/**
	 * ????????????????????????
	 * @return
	 */
	public String userRe(){
		user = userService.get(id);
		return "userreset";
	}
	
	/**
	 * ??????????????????
	 * @return
	 */
	public String userReset(){
		String password = SafeUtil.encode(user.getPassword());
		user = userService.get(user.getId());
		user.setPassword(password);
		userService.update(user);
		return "reuser";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String userUp(){
		user = userService.get(id);
		return "userupdate";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String userUpdate(){
		userService.update(user);
		return "reuser";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String userDelete(){
		userService.delete(id);
		return "reuser";
	}
	
	
	/**
	 * ????????????
	 * @return
	 */
	public String bookList(){
		bookList = bookService.getList(status, page, rows);
		pageTool = PageUtil.getPageToolAdmin(servletRequest, bookService.getTotal(status), page, rows);
		return "book";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String bookAd(){
		categoryList = categoryService.getList();
		return "bookadd";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String bookAdd(){
		book.setCover(UploadUtil.fileUpload(photo, photoFileName, "picture"));
		bookService.add(book);
		return "rebook";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String bookUp(){
		categoryList = categoryService.getList();
		book = bookService.get(id);
		return "bookupdate";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String bookUpdate(){
		if (photo != null) {
			book.setCover(UploadUtil.fileUpload(photo, photoFileName, "picture"));
		}
		bookService.update(book);
		return "rebook";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String bookDelete(){
		bookService.delete(book);
		return "rebook";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String bookSet(){
		book = bookService.get(id);
		switch (flag) {
		case 10:
			book.setSpecial(false);
			break;
		case 11:
			book.setSpecial(true);
			break;
		case 20:
			book.setNews(false);
			break;
		case 21:
			book.setNews(true);
			break;
		case 30:
			book.setSale(false);
			break;
		case 31:
			book.setSale(true);
			break;
		}
		bookService.update(book);
		return "rebook";
	}
	
	
	/**
	 * ????????????
	 * @return
	 */
	public String categoryList(){
		categoryList = categoryService.getList(page, rows);
		pageTool = PageUtil.getPageToolAdmin(servletRequest, categoryService.getTotal(), page, rows);
		return "category";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String categoryAdd(){
		categoryService.add(category);
		return "recategory";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String categoryUp(){
		category = categoryService.get(id);
		return "categoryupdate";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String categoryUpdate(){
		categoryService.update(category);
		return "recategory";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String categoryDelete(){
		categoryService.delete(category);
		return "recategory";
	}
	
	
	/**
	 * ???????????????
	 * @return
	 */
	public String adminList(){
		adminList = adminService.getList(page, rows);
		pageTool = PageUtil.getPageToolAdmin(servletRequest, adminService.getTotal(), page, rows);
		return "admin";
	}
	
	/**
	 * ???????????????
	 * @return
	 */
	public String adminAdd(){
		if (adminService.isExist(admin.getUsername())) {
			addActionError("??????????????????!");
			return "adminadd";
		}
		adminService.add(admin);
		return "readmin";
	}
	
	/**
	 * ??????????????????
	 * @return
	 */
	public String adminRe(){
		admin = adminService.get(id);
		return "adminreset";
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public String adminReset(){
		admin.setPassword(SafeUtil.encode(admin.getPassword()));
		adminService.update(admin);
		return "readmin";
	}
	
	/**
	 * ???????????????
	 * @return
	 */
	public String adminDelete(){
		adminService.delete(admin);
		return "readmin";
	}
	
	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public List<Book> getBookList() {
		return bookList;
	}

	public void setBookList(List<Book> bookList) {
		this.bookList = bookList;
	}

	public List<Indent> getIndentList() {
		return indentList;
	}

	public void setIndentList(List<Indent> indentList) {
		this.indentList = indentList;
	}

	public List<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<Category> categoryList) {
		this.categoryList = categoryList;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Items> getItemList() {
		return itemList;
	}

	public void setItemList(List<Items> itemList) {
		this.itemList = itemList;
	}

	public List<Users> getUserList() {
		return userList;
	}

	public void setUserList(List<Users> userList) {
		this.userList = userList;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public Book getBook() {
		return book;
	}

	public List<Admin> getAdminList() {
		return adminList;
	}

	public void setAdminList(List<Admin> adminList) {
		this.adminList = adminList;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public File getPhoto() {
		return photo;
	}

	public void setPhoto(File photo) {
		this.photo = photo;
	}

	public String getPhotoFileName() {
		return photoFileName;
	}

	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}

	public String getPhotoContentType() {
		return photoContentType;
	}

	public void setPhotoContentType(String photoContentType) {
		this.photoContentType = photoContentType;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

}
