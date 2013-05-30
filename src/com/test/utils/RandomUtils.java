package com.test.utils;

import java.util.Random;

public class RandomUtils {
	
	
	
	/**
	 * 生成随机的号码
	 * @return
	 */
	public String numberRandom(){
		Random random = new Random();
		String numberRandom = null;
		String[] arry = {"131","134","135","136","158","159","186","188"};
		int m = random.nextInt(arry.length-1);
		int i = 10000000 + random.nextInt(89999999);
		numberRandom = arry[m]+String.valueOf(i);
		System.out.println(numberRandom);
		return numberRandom;
	}

	
	/**
	 * 
	 * 方法名：随机生成中文姓名
	 * 
	 * @return
	 */
	public String chineseNameRandom() {

		String name = null;

		final String[] firstNames = { "赵", "钱", "孙", "李", "周", "吴", "郑", "王",
				"冯", "陈", "诸 ", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
				"何", "吕", "施", "张", "孔", "曹", "严 ", "华", "金", "魏", "陶", "姜",
				"戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘 ", "葛",
				"奚", "范", "彭", "郎", "鲁", "韦", "昌", "马", "苗", "凤", "花", "方",
				"俞", "任", "袁 ", "柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛",
				"雷", "贺", "倪", "汤", "滕", "殷", "罗 ", "毕", "郝", "邬", "安", "常",
				"乐", "于", "时", "傅", "皮", "卡", "齐", "康", "伍", "余", "元 ", "卜",
				"顾", "孟", "平", "黄", "和", "穆", "萧", "尹", "姚", "邵", "堪", "汪",
				"祁", "毛", "禹 ", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴",
				"谈", "宋", "茅", "庞", "熊", "纪", "舒 ", "屈", "项", "祝", "董", "粱",
				"杜", "阮", "蓝", "闵", "席", "季", "麻", "强", "贾", "路", "娄 ", "危",
				"江", "童", "颜", "郭", "梅", "盛", "林", "刁", "钟", "徐", "邱", "骆",
				"高", "夏", "蔡 ", "田", "樊", "胡", "凌", "霍", "虞", "万", "支", "柯",
				"咎", "管", "卢", "莫", "经", "房", "裘 ", "缪", "干", "解", "应", "宗",
				"丁", "宣", "贲", "邓", "郁", "单", "杭", "洪", "包", "诸", "左 ", "石",
				"崔", "吉", "钮", "龚", "程", "嵇", "邢", "滑", "裴", "陆", "荣", "翁",
				"荀", "羊", "於 ", "惠", "甄", "魏", "家", "封", "芮", "羿", "储", "靳",
				"汲", "邴", "糜", "松", "井", "段", "富 ", "巫", "乌", "焦", "巴", "弓",
				"牧", "隗", "山", "谷", "车", "侯", "宓", "蓬", "全", "郗", "班 ", "仰",
				"秋", "仲", "伊", "宫", "宁", "仇", "栾", "暴", "甘", "钭", "厉", "戎",
				"祖", "武", "符 ", "刘", "景", "詹", "束", "龙", "司马", "上官", "欧阳",
				"夏侯", "诸葛", "东方", "尉迟", "公孙 ", "长孙", "慕容", "司徒", "西门" };
		final String[] lastNames = { "超", "媛", "念", "立", "思", "嘉", "雨", "伟",
				"权", "秋", "佩 ", "雅", "联", "如", "渠", "保", "室", "铜", "梧", "胤",
				"昱", "佳", "伊", "校", "诗", "节", "如 ", "阁", "耕", "宫", "古", "谷",
				"观", "桂", "贵", "国", "广", "冠 ", "汉", "翰", "航", "杭", "海", "豪",
				"浩", "皓", "和", "河", "贺", "恒", "弘", "虹", "宏", "红 ", "厚", "鹄",
				"虎", "华", "欢", "怀", "辉", "惠", "会", "奇", "吉 ", "骥", "嘉", "佳",
				"甲", "稼", "江", "坚", "剑", "锦", "经", "镜", "界", "竞", "介", "京",
				"建 ", "净", "精", "敬", "静", "靖", "津", "进", "菁", "景", "炯", "驹 ",
				"举", "炬", "君", "俊", "军", "骏", "郡", "峻", "恺", "楷", "康", "可",
				"克", "珂", "逵", "魁 ", "阔", "昆", "奎", "宽", "况", "乐", "雷", "岭",
				"廉", "霖", "麟 ", "灵", "利", "良", "联", "烈", "罗", "陵", "梁", "立",
				"礼", "力", "莉", "烁", "隆", "龙", "禄 ", "璐", "露", "律", "茂", "梦",
				"密", "铭", "明", "绵", "妙", "默 ", "木", "能", "年", "宁", "念", "怒",
				"庞", "佩", "培", "朋", "鹏", "屏", "平", "魄", "珀", "璞 ", "奇", "琦",
				"齐", "启", "谦", "乾", "茜", "倩", "芹", "琴", "青 ", "卿", "秋", "权",
				"求", "情", "渠", "全", "荃", "群", "泉", "然", "让", "仁", "任", "荣",
				"儒 ", "锐", "若", "瑞", "三", "瑟", "森", "韶", "绍", "尚", "商", "珊 ",
				"善", "生", "升", "声", "晟", "胜", "盛", "诗", "时", "石", "实", "凇",
				"慎", "设", "守", "随 ", "世", "寿", "仕", "余", "帅", "双", "朔", "硕",
				"水", "誓", "适 ", "书", "舒", "殊", "顺", "思", "嗣", "似", "松", "颂",
				"素", "岁", "棠", "泰", "腾", "添", "铁 ", "同", "桐", "童", "彤", "团",
				"涂", "图", "土", "万", "旺", "望 ", "王", "闻", "威", "薇", "嵬", "伟",
				"卫", "蔚", "文", "微", "巍", "玮", "为", "畏", "吾", "午 ", "西", "熙",
				"玺", "仙", "先", "孝", "湘", "祥", "行", "献", "享 ", "效", "兴", "夏",
				"宣", "协", "向", "校", "轩", "瑕", "衔", "筱", "羡", "相", "香", "贤",
				"翔 ", "杏", "新", "信", "幸", "心", "星", "绣", "秀", "欣", "鑫", "兴 ",
				"行", "雄", "许", "炫", "雪", "学", "旭", "璇", "勋", "萱", "迅", "亚",
				"雅", "扬", "耀", "彦 ", "延", "岩", "炎", "永", "砚", "演", "焱", "洋",
				"阳", "曜", "耀 ", "夜", "译", "逸", "伊", "义", "艺", "意", "异", "怡",
				"翼", "毅", "银", "瑛", "仪", "易", "寅 ", "印", "苡", "野", "业", "英",
				"璎", "盈", "颖", "影", "雍", "勇 ", "悠", "由", "游", "佑", "友", "瑜",
				"遇", "玉", "岳", "元", "宇", "愚", "钰", "裕", "郁", "于 " };
		int lastNamesLength = lastNames.length - 1;
		int firstNameLength = firstNames.length - 1;
		Random random = new Random();
		System.out.println(lastNamesLength + " " + firstNameLength);
		try {
			int r1 = random.nextInt(lastNamesLength);
			int r2 = random.nextInt(firstNameLength);
			int r3 = random.nextInt(firstNameLength);
			System.out.println(r1 + " " + r2 + " " + r3);
			int number = random.nextInt(1);
			if (number  == 0) {
				name = firstNames[r1].concat(lastNames[r2]);
			} else {
				name = firstNames[r1] + lastNames[r2] + lastNames[r3];
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			name = "张三";
		}

		return name;

	}

	/**
	 * 
	 * 方法名：随机生成英文姓名
	 * 
	 * @return
	 */
	public String englishNameRandom() {

		String name = null;

		final String[] firstNames = { "Mary", "Patricia", "Linda", "Barbara",
				"Elizabeth", "Jennifer", "Maria", "Susan", "Margaret",
				"Dorothy", "Lisa", "Nancy", "Karen", "Betty", "Helen",
				"Sandra", "Donna", "Carol", "Ruth", "Sharon", "Michelle",
				"Laura", "Sarah", "Kimberly", "Deborah", "Jessica", "Shirley",
				"Cynthia", "Angela", "Melissa", "Brenda", "Amy", "Anna",
				"Rebecca", "Virginia", "Kathleen", "Pamela", "Martha", "Debra",
				"Amanda", "Stephanie", "Carolyn", "Christine", "Marie",
				"Janet", "Catherine", "Frances", "Ann", "Joyce", "Diane",
				"Alice", "Julie", "Heather", "Teresa", "Doris", "Gloria",
				"Evelyn", "Jean", "Cheryl", "Mildred", "Katherine", "Joan",
				"Ashley", "Judith", "Rose", "Janice", "Kelly", "Nicole",
				"Judy", "Christina", "Kathy", "Theresa", "Beverly", "Denise",
				"Tammy", "Irene", "Jane", "Lori", "Rachel", "Marilyn",
				"Andrea", "Kathryn", "Louise", "Sara", "Anne", "Jacqueline",
				"Wanda", "Bonnie", "Julia", "Ruby", "Lois", "Tina", "Phyllis",
				"Norma", "Paula", "Diana", "Annie", "Lillian", "Emily",
				"Robin", "Peggy", "Crystal", "Gladys", "Rita", "Dawn",
				"Connie", "Florence", "Tracy", "Edna", "Tiffany", "Carmen",
				"Rosa", "Cindy", "Grace", "Wendy", "Victoria", "Edith", "Kim",
				"Sherry", "Sylvia", "Josephine", "Thelma", "Shannon", "Sheila",
				"Ethel", "Ellen", "Elaine", "Marjorie", "Carrie", "Charlotte",
				"Monica", "Esther", "Pauline", "Emma", "Juanita", "Anita",
				"Rhonda", "Hazel", "Amber", "Eva", "Debbie", "April", "Leslie",
				"Clara", "Lucille", "Jamie", "Joanne", "Eleanor", "Valerie",
				"Danielle", "Megan", "Alicia", "Suzanne", "Michele", "Gail",
				"Bertha", "Darlene", "Veronica", "Jill", "Erin", "Geraldine",
				"Lauren", "Cathy", "Joann", "Lorraine", "Lynn", "Sally",
				"Regina", "Erica", "Beatrice", "Dolores", "Bernice", "Audrey",
				"Yvonne", "Annette", "June", "Samantha", "Marion", "Dana",
				"Stacy", "Ana", "Renee", "Ida", "Vivian", "Roberta", "Holly",
				"Brittany", "Melanie", "Loretta", "Yolanda", "Jeanette",
				"Laurie", "Katie", "Kristen", "Vanessa", "Alma", "Sue",
				"Elsie", "Beth", "Jeanne", "James", "John", "Robert",
				"Michael", "William", "David", "Richard", "Charles", "Joseph",
				"Thomas", "Christopher", "Daniel", "Paul", "Mark", "Donald",
				"George", "Kenneth", "Steven", "Edward", "Brian", "Ronald",
				"Anthony", "Kevin", "Jason", "Matthew", "Gary", "Timothy",
				"Jose", "Larry", "Jeffrey", "Frank", "Scott", "Eric",
				"Stephen", "Andrew", "Raymond", "Gregory", "Joshua", "Jerry",
				"Dennis", "Walter", "Patrick", "Peter", "Harold", "Douglas",
				"Henry", "Carl", "Arthur", "Ryan", "Roger", "Joe", "Juan",
				"Jack", "Albert", "Jonathan", "Justin", "Terry", "Gerald",
				"Keith", "Samuel", "Willie", "Ralph", "Lawrence", "Nicholas",
				"Roy", "Benjamin", "Bruce", "Brandon", "Adam", "Harry", "Fred",
				"Wayne", "Billy", "Steve", "Louis", "Jeremy", "Aaron", "Randy",
				"Howard", "Eugene", "Carlos", "Russell", "Bobby", "Victor",
				"Martin", "Ernest", "Phillip", "Todd", "Jesse", "Craig",
				"Alan", "Shawn", "Clarence", "Sean", "Philip", "Chris",
				"Johnny", "Earl", "Jimmy", "Antonio", "Danny", "Bryan", "Tony",
				"Luis", "Mike", "Stanley", "Leonard", "Nathan", "Dale",
				"Manuel", "Rodney", "Curtis", "Norman", "Allen", "Marvin",
				"Vincent", "Glenn", "Jeffery", "Travis", "Jeff", "Chad",
				"Jacob", "Lee", "Melvin", "Alfred", "Kyle", "Francis",
				"Bradley", "Jesus", "Herbert", "Frederick", "Ray", "Joel",
				"Edwin", "Don", "Eddie", "Ricky", "Troy", "Randall", "Barry",
				"Alexander", "Bernard", "Mario", "Leroy", "Francisco",
				"Marcus", "Micheal", "Theodore", "Clifford", "Miguel", "Oscar",
				"Jay", "Jim", "Tom", "Calvin", "Alex", "Jon", "Ronnie", "Bill",
				"Lloyd", "Tommy", "Leon", "Derek", "Warren", "Darrell",
				"Jerome", "Floyd", "Leo", "Alvin", "Tim", "Wesley", "Gordon",
				"Dean", "Greg", "Jorge", "Dustin", "Pedro", "Derrick", "Dan",
				"Lewis", "Zachary", "Corey", "Herman", "Maurice", "Vernon",
				"Roberto", "Clyde", "Glen", "Hector", "Shane", "Ricardo",
				"Sam", "Rick", "Lester", "Brent", "Ramon", "Charlie", "Tyler",
				"Gilbert", "Gene" };
		final String[] lastNames = { "Smith", "Johnson", "Williams", "Jones",
				"Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor",
				"Anderson", "Thomas", "Jackson", "White", "Harris", "Martin",
				"Thompson", "Garcia", "Martinez", "Robinson", "Clark",
				"Rodriguez", "Lewis", "Lee", "Walker", "Hall", "Allen",
				"Young", "Hernandez", "King", "Wright", "Lopez", "Hill",
				"Scott", "Green", "Adams", "Baker", "Gonzalez", "Nelson",
				"Carter", "Mitchell", "Perez", "Roberts", "Turner", "Phillips",
				"Campbell", "Parker", "Evans", "Edwards", "Collins", "Stewart",
				"Sanchez", "Morris", "Rogers", "Reed", "Cook", "Morgan",
				"Bell", "Murphy", "Bailey", "Rivera", "Cooper", "Richardson",
				"Cox", "Howard", "Ward", "Torres", "Peterson", "Gray",
				"Ramirez", "James", "Watson", "Brooks", "Kelly", "Sanders",
				"Price", "Bennett", "Wood", "Barnes", "Ross", "Henderson",
				"Coleman", "Jenkins", "Perry", "Powell", "Long", "Patterson",
				"Hughes", "Flores", "Washington", "Butler", "Simmons",
				"Foster", "Gonzales", "Bryant", "Alexander", "Russell",
				"Griffin", "Diaz", "Hayes", "Myers", "Ford", "Hamilton",
				"Graham", "Sullivan", "Wallace", "Woods", "Cole", "West",
				"Jordan", "Owens", "Reynolds", "Fisher", "Ellis", "Harrison",
				"Gibson", "Mcdonald", "Cruz", "Marshall", "Ortiz", "Gomez",
				"Murray", "Freeman", "Wells", "Webb", "Simpson", "Stevens",
				"Tucker", "Porter", "Hunter", "Hicks", "Crawford", "Henry",
				"Boyd", "Mason", "Morales", "Kennedy", "Warren", "Dixon",
				"Ramos", "Reyes", "Burns", "Gordon", "Shaw", "Holmes", "Rice",
				"Robertson", "Hunt", "Black", "Daniels", "Palmer", "Mills",
				"Nichols", "Grant", "Knight", "Ferguson", "Rose", "Stone",
				"Hawkins", "Dunn", "Perkins", "Hudson", "Spencer", "Gardner",
				"Stephens", "Payne", "Pierce", "Berry", "Matthews", "Arnold",
				"Wagner", "Willis", "Ray", "Watkins", "Olson", "Carroll",
				"Duncan", "Snyder", "Hart", "Cunningham", "Bradley", "Lane",
				"Andrews", "Ruiz", "Harper", "Fox", "Riley", "Armstrong",
				"Carpenter", "Weaver", "Greene", "Lawrence", "Elliott",
				"Chavez", "Sims", "Austin", "Peters", "Kelley", "Franklin",
				"Lawson" };
		int lastNamesLength = lastNames.length - 1;
		int firstNameLength = firstNames.length - 1;
		Random random = new Random();
		System.out.println(lastNamesLength + " " + firstNameLength);
		try {
			int r1 = random.nextInt(lastNamesLength);
			int r2 = random.nextInt(firstNameLength);
			System.out.println(r1 + " " + r2);
			name = firstNames[r1] + " " + lastNames[r2];
		} catch (Exception e) {
			// TODO: handle exception
			name = "Miacheal jakson";
			e.printStackTrace();
		}

		return name;

	}
	
	/**
	 * 生成随机的邮件地址
	 * @return
	 */
	public String emailRandom(){
		String[] address = {"@gmail.com","@yahoo.cn","@msn.com","@hotmal.com","@qq.com","@163.com","@yahoo.com"};
		String email = "";
		int addressLength = address.length - 1;
		Random random = new Random();
		try {
			int r1 = random.nextInt(addressLength);
			email = this.noteRandom() + address[r1];
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			email = "hwtest@gionee.com";
		}
		System.out.println(email);
		return email;
	}
	
	/**
	 * 生成随机的字串
	 * @return
	 */
	public String noteRandom(){
		String[] name = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v",
				"w","x","y","z","0","1","2","3","4","5","6","7","8","9"};
		String note= "";
		int nameLength = name.length - 1;
		Random random = new Random();
		try {
			int r3 = 6+random.nextInt(6);
			for(int i = 0;i < r3;i++){
				int r2 = random.nextInt(nameLength);
				note =note.concat(name[r2]);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			note = "hello friend";
		}
		return note;
	}
	
	/**
	 * 生成随机的短信内容
	 * @return
	 */
	public String contentRandom(int number){
		String message = null;
		final String[] seventy = {"前方有着希望，后方住着坚强，单纯的生活快快乐乐；左手拿着健康，右手抓着平安，简单的幸福双手在握；发条短信，祝福问候，浓浓的情谊长长久久。" ,
				"天苍苍，野茫茫，秋去冬来落叶黄，现代生活节奏忙，做人别做工作狂，添衣保暖甲流防，保重身体业辉煌，前途似锦显芬芳，幸福人生长长长。",
				"穿毛衣，穿棉衣，咱得笑傲冷空气，戴个帽，戴手套，防寒保暖很重要，勤锻炼，多喝汤，这样才能保健康，朋友，降温下雪了，祝你快乐过冬。",
				"把持不住思念的心跳，让我深切体验牵挂的美妙，送你一件用心织成的温暖外套，用祝福将你笼罩，愿快乐永远在你怀抱，幸福放声欢笑。",
				"在冰冷的冬日里，就算没有寒风骤起，没有雪花飘起，你也要好好保重自己，照顾好你的身体，我的惦记伴随你快乐如意，冬日开心，时刻欢喜。",
				"一角钱买不到甜美玫瑰，一角钱买不到幽香咖啡，一角钱买不到温暖手套，一角钱能买到祝福的微笑，一角钱阻挡不了寒流的入侵，一角钱能送去寒冬的关心。",
				"北方乌云密布，冷气袭击中东部；大风大雪和大雾，出行放慢您脚步；老人儿童和病人，注意保暖夜晚清晨；大幅降温寒人心，幸好及时来短信打开温暖多开心",
				"春水荡漾，荡去你的惆怅；碧草轻摇，摇走你的忧伤。春天闪亮登场，将快乐温馨送到你的心上。愿你心情芬芳，生活顺畅，幸福绵长！",
				"美好春天翩翩来到，春花铺起祝福桥，我的短信在桥上跑，直接找你来报道。愿平安将你拥抱，快乐将你围绕，无边的幸福永远将你笼罩！",
				"风吹落叶，落叶飘远，冬天已经来到身边，给你最温馨的思念，愿你整个冬天都温暖，快乐洋溢着幸福之船，愿你幸福永不变，开心到永远。",
				"没有加薪，没有升职，连年终奖金都被取消了，打算借酒浇愁一回。有人带了瓶酒来，大家一看那酒的名字，眼泪就都扑簌簌地往下掉开了，还有人顿时抱头痛哭了一场。那酒的名字叫——老白干.....",
				"一次爬山，累了靠着大树休息，看到许多蚂蚁在搬家，不管路途多么艰难险恶，嘴里使劲咬住东西，撅着屁股使劲拖，滚下来又爬上去，甚为感动，于是......我站起身，一泡尿把蚂蚁冲走了......",
				"天气热了，为防中暑，请该钻土的钻土，该褪毛的褪毛，想y|奔的y|奔，想秃头的秃头。如果啥都不干，啥都不想，恭喜你，你已练成“心静自然凉”神功。",
				"床上一片汪洋，此时仍在梦乡。惊醒伸手一摸，被褥潮湿冰凉。若被老妈知道，一顿暴K难防。索性决定用体温捂干，装病不要起床。现在是冬天，呜呜...",
				"^o^把我的祝福，开成一朵鲜艳的花，绽放在你生命的天空下，为你的生活增添一抹亮丽的色彩，为你的日子带去一丝甜蜜的芳香，愿你的每一天，快乐平安健康。",
				"^o^岁月的年轮，转动着永不停歇；旧日的情义，思念着永不忘怀；美好的祝愿，问候着永不间断；快乐的心情，持续着永不消失。祝福你在轻松随意的每一天。",
				"^o^有了手机，却少了沟通的语言，有了短信，却承载不了太多的思念。习惯了等待你的QQ上线，却无法保证休闲在同一时间。远方的挂念，传送于指尖。",
				"^o^好茶，可以凉人；好风，可以爽人，好荫，可以福人；好雨，可以浴人。夏暑到，愿好风、好雨、好荫陪伴你，愿好事、好运、好意随着你。",
				"本人在IT公司工作。IT人大家都懂的，天天加班，熬夜常态，更无双休可能。一个周末，大家一起忙了一上午，十一点多，某同事忽然起身，丢下一句话就冲出去了。他说：你们忙着，我出去结个婚就回来。。。",
				"踩一踩春天的尾巴，牵一牵夏天的衣袖；收一收春天的膘肉，紧一紧夏天的裤口。借夏天的风，吹吹春天的愁，借夏天的口，祝福牵挂的你！ ",
				"^o^ 最近好吗？天气渐热，昼暖夜凉，温差较大，感冒横行！可以旅游，选择山清水秀；可以逛街，选择典雅清幽；总之就是：常常微笑，身体棒棒！",
				"^o^抬头见喜，低头捡钱。直立好运，弯腰幸福。扭头富贵，转身吉祥。伸手开心，蹬腿快乐。静坐安心，运动舒畅。不论东南西北，坐立行走，祝你好运时时有。 ",
				"前方有着希望，后方住着坚强，单纯的生活快快乐乐；左手拿着健康，右手抓着平安，简单的幸福双手在握；发条短信，祝福问候，浓浓的情谊长长久久",
				"蓝蓝的天空有朵朵的云，朵朵的云里有淡淡的诗，淡淡的诗里有浓浓的情，浓浓的情里有我深深的问候，祝你幸福快乐！",
				"如果心情有开关，愿你永远开着快乐与幸福，关闭痛苦与烦忧；开着微笑与得意，关闭愁容与失意。愿你保持好心境，常常多笑意！"};
		final String[] oneHundredsixty = {"the more you fight something, the more anxious you become ---the more you''re involved in a bad pattern, the more difficult it is to escape. (seebohm caroline, british physician)",
				" let us suggest to the person in crisis that he cease concentrating so upon the dangers involved and the difficultie，and concentrate instead upon the opptunity---for there is always opportunity in crisis. (seebohm caroline, british physician)",
				" everything can be taken from a man but one thing; the freedom to choose his attitude in any given set of circumstances. (leonhard frand , german novelist)",
				"mishaps are like knives that either serve us or cut us as we grasp them by the handle or blade.(james russell lowell, american poetess and critic)",
				" You know, we don't grow most of the food we eat. We wear clothes other people make. We speak a language that other people developed. We use a mathematics that other people evolved... I mean, we're constantly taking things. It's a wonderful, ecstatic feeling to create something that puts it back in the pool of human experience and knowledge.",
				" The only way to do great work is to love what you do. If you haven't found it yet, keep looking. Don't settle. As with all matters of the heart, you'll know when you find it.",
				"Your time is limited, so don't waste it living someone else's life. Don't be trapped by dogma - which is living with the results of other people's thinking. Don't let the noise of other's opinions drown out your own inner voice. And most important, have the courage to follow your heart and intuition. They somehow already know what you truly want to become. Everything else is secondary.",
				" I'm the only person I know that's lost a quarter of a billion dollars in one year... It's very character-building.",
				"I would trade all of my technology for an afternoon with Socrates.",
				"There's a phrase in Buddhism, 'Beginner's mind.' It's wonderful to have a beginner's mind.",
				"We're here to put a dent in the universe. Otherwise why else even be here?",
				"Be a yardstick of quality. Some people aren't used to an environment where excellence is expected.",
				"We think basically you watch television to turn your brain off, and you work on your computer when you want to turn your brain on.",
				"Innovation distinguishes between a leader and a follower.",
				"What does little birdie say,In her nest at peep of day? Let me fly, says little birdie, Mother, let me fly away, Birdie, rest a little longer, Till the little wings are stronger. So she rests a little longer, Then she flies away.",
				"What does little baby say, In her bed at peep of day? Baby says, like little birdie, Let me rise and fly away. Baby, sleep a little longer, Till the little limbs are stronger. If she sleeps a little longer, Baby too shall fly away."};	
		final String[] eightHundreds = {"HelHello.  Welcome to another look at our world “as it is.”  I’m Jim Tedder in Washington. Today we have some exciting news from India, as that country’s space program turns its attention to the “red planet.”  We will also hear about violence in Liberia against women and girls, and what is being done about it. But our first subject is money. President Obama recently called for a rise in the federally required minimum wage in the United States. Currently, the lowest legal wage is seven dollars 25 cents an hour. The president wants it to rise to nine dollars an hour. His call gave new energy to labor activists, including those who say the pay raise would especially help women. American activists are pressing the government to raise the minimum w. “Raise the wage!”governm"
				,"President Obama supported this call in a recent television broadcast. A family with two kids that earns the minimum wage still lives below the poverty line. That’s wrong.” Joan Entmacher is with the National Women’s Law Center. She says American women hold more than half the nation’s minimum wage jobs, but they represent about half the American workforce. “They’re the people who serve you food in restaurants or prepare it behind the scenes. They’re the people who clean your offices at night after you go home or clean your hotel rooms. Those are the really low paid jobs, and they are overwhelmingly filled by women.” Joan Entmacher says women have the most to gain if the minimum wage is raised. Joan Entmacher says women have the most to gain if the minimum wage is. "};
		
		int seventyLength = seventy.length-1;
		int oneHundredsixtyLength = oneHundredsixty.length-1;
		int eightHundredsLength = eightHundreds.length-1;
		switch (number) {
		case 0:
			message = seventy[(int)Math.round(Math.random()*seventyLength)];
			break;
		case 1:
			message = oneHundredsixty[(int)Math.round(Math.random()*oneHundredsixtyLength)];
			break;
		case 2:
			message = eightHundreds[(int)Math.round(Math.random()*eightHundredsLength)];
			break;
		default:
			break;
		}
		return message;
		
	}
	
}
