import type { Trip } from "../types/trip";

/**
 * MOCK DATA – ใช้แทนผลลัพธ์จากฐานข้อมูลจริงชั่วคราว
 */
const MOCK_TRIPS: Trip[] = [
  {
    id: 1,
    title: "คู่มือเที่ยวเกาะช้าง กิน เที่ยว พักที่ไหนดี? อ่านจบครบที่เดียว!",
    description:
      "วันว่างนี้ไปเที่ยวเกาะช้างกัน พร้อมทำกิจกรรมต่าง ๆ เช่น เที่ยวน้ำตก ล่องเรือชมป่าชายเลน ขี่ช้างท่องป่า ผจญภัยเหนือยอดไม้ และดำน้ำตื้น รับรอทริปนี้สนุกแน่!\n\n“เกาะช้าง” จังหวัดตราด ที่เที่ยวทะเลใกล้กรุงเทพฯ สามารถเที่ยวกันได้ทุกฤดู เคลียร์งานและวันว่างได้แล้วก็แค่จัดกระเป๋าไปกันได้เลยกับแพลน เที่ยวเกาะช้าง ต้องไปกิน เที่ยว พักที่ไหน? อ่านจบครบที่เดียว! ซึ่งหลายคนสงสัยว่าไปเกาะช้างเที่ยวไหนดี? Wongnai Travel บอกเลยเกาะช้างไม่ได้มีแค่ไปเล่นน้ำทะเล หรือนอนพักริมหาดทรายเท่านั้น เพราะมีกิจกรรมสนุก ๆ รออยู่เพียบ...",
    photos: [
      "https://img.wongnai.com/p/1600x0/2019/07/02/3c758646aa6c426ba3c6a81f57b20bd6.jpg",
      "https://img.wongnai.com/p/1600x0/2019/07/02/6a2733ab91164ac8943b77deb14fdbde.jpg",
      "https://img.wongnai.com/p/1600x0/2019/07/02/941dbb607f0742bbadd63bbbd993e187.jpg",
      "https://img.wongnai.com/p/1600x0/2019/07/02/bd1d256256c14c208d0843a345f75741.jpg",
    ],
    tags: ["เกาะ", "ทะเล", "จุดชมวิว", "ธรรมชาติ", "ตราด"],
    latitude: 12.048,
    longitude: 102.3225,
    province: "ตราด",
    url: "https://www.wongnai.com/articles/koh-chang-guide",
  },
  {
    id: 2,
    title: "ลัดเลาะ 10 ที่เที่ยวย่าน BTS สายสีเขียว",
    description:
      "BTS สายสีเขียวเปิดให้บริการเพิ่ม 5 สถานีทั้งที งานนี้ต้องไม่มีพลาด รีบไปอัปเดต เช็กอินที่เที่ยวคูล ๆ ไปได้ง่ายก่อนใคร\n\nเริ่มกันที่ ‘สถานีห้าแยกลาดพร้าว’ เป็นสถานีที่เชื่อมกับเซ็นทรัลลาดพร้าว ...",
    photos: [
      "https://img.wongnai.com/p/1600x0/2020/02/18/458b9a31b62b408d91137fbe152f7450.jpg",
      "https://img.wongnai.com/p/1600x0/2020/02/18/08bca1c784db4d7f92e93656b5376681.jpg",
      "https://img.wongnai.com/p/1600x0/2020/02/18/95bd2ca371e24436bcb24d432a86836c.jpg",
      "https://img.wongnai.com/p/1600x0/2020/02/18/68289e188ea54e00b5e9d806cfd0fc54.jpg",
    ],
    tags: ["คาเฟ่", "ร้านกาแฟ", "จุดถ่ายรูป", "เที่ยวใกล้กรุง", "กรุงเทพมหานคร"],
    latitude: 13.8121,
    longitude: 100.553,
    province: "กรุงเทพมหานคร",
    url: "https://www.wongnai.com/articles/bts-green-line-travel",
  },
  {
    id: 3,
    title: "เที่ยวทุ่งทานตะวันชลบุรี ได้รูปสวยไม่ต้องไปไกลที่ ไร่คีรีบูรพา",
    description:
      "บานสะพรั่งแล้วจ้า กับทุ่งทานตะวันที่ใหญ่กว่า 30 ไร่ แต่อย่าชะล่าใจไป เพราะว่าเปิดให้เข้าชมถึงแค่เดือนมกราคมเท่านั้น...",
    photos: [
      "https://img.wongnai.com/p/1600x0/2019/12/27/777681c6184b498baead821895f7d7ab.jpg",
      "https://img.wongnai.com/p/1600x0/2019/12/27/10b2aaf1f0df42a98d0บ482e254789ee.jpg",
      "https://img.wongnai.com/p/1600x0/2019/12/27/79e5b71efa54433b909e08bac13f27bb.jpg",
      "https://img.wongnai.com/p/1600x0/2019/12/27/939f644df3d847409a85c1b2d054af25.jpg",
    ],
    tags: ["จุดถ่ายรูป", "ถ่ายรูปสวย", "เที่ยวใกล้กรุง", "ชลบุรี"],
    latitude: 13.2528,
    longitude: 101.0156,
    province: "ชลบุรี",
    url: "https://www.wongnai.com/articles/kiriburapha-sunflower-field",
  },
  {
    id: 4,
    title:
      "เที่ยวสิ้นปี บ้านอีต่องเหมืองปิล็อก ชมหมอก กินหมูกระทะ ล่าทางช้างเผือก",
    description:
      "จังหวะ จะเที่ยว บ้านอีต่อง เหมืองปิล็อก ตามล่าทะเลหมอกหน้าหนาว ...",
    photos: [
      "https://img.wongnai.com/p/1600x0/2019/12/25/54961e4326954765a80cd20e2044083d.jpg",
      "https://img.wongnai.com/p/1600x0/2019/12/25/183af5673b074e55a3842aca97676220.jpg",
      "https://img.wongnai.com/p/1600x0/2019/12/25/9bbcb032afc145d19e485defcf2067c1.jpg",
      "https://img.wongnai.com/p/1600x0/2019/12/25/2974828fdb16492da0e8f35f627ade7a.jpg",
    ],
    tags: [
      "จุดถ่ายรูป",
      "หมู่บ้าน",
      "ภูเขา",
      "ธรรมชาติ",
      "ถ่ายรูปสวย",
      "บ้านอีต่องเหมืองปิล็อก",
      "กาญจนบุรี",
    ],
    latitude: 14.6761,
    longitude: 98.3814,
    province: "กาญจนบุรี",
    url: "https://www.wongnai.com/articles/ban-e-tong-pilok",
  },
  {
    id: 5,
    title:
      "ทริปที่เที่ยวและร้านกาแฟเชียงใหม่ ที่สวยได้แบบไม่ต้องไปไกลถึงเกาหลี",
    description: "แวะคาเฟ่เชียงใหม่! เที่ยวสไตล์เกาหลีแบบไม่ต้องไปไกล ...",
    photos: [
      "https://img.wongnai.com/p/1600x0/2019/10/10/3092e773cda34ca2a77373b82f7639b7.jpg",
      "https://img.wongnai.com/p/1600x0/2019/10/10/7739627a33134587827ee72f1a9c2cce.jpg",
      "https://img.wongnai.com/p/1600x0/2019/10/10/e51a7af0bd18463db0685057809368ef.jpg",
      "https://img.wongnai.com/p/1600x0/2019/10/10/49c6a0f4c25a41b08b3ca47ce410195d.jpg",
    ],
    tags: ["จุดถ่ายรูป", "ถ่ายรูปสวย", "ร้านกาแฟ", "เชียงใหม่", "คาเฟ่"],
    latitude: 18.7883,
    longitude: 98.9853,
    province: "เชียงใหม่",
    url: "https://www.wongnai.com/articles/chiang-mai-cafe-korea-style",
  },
  {
    id: 6,
    title: "เที่ยวเกาะหลีเป๊ะ กิน เที่ยว พักที่ไหน? จัดมาให้ครบ!",
    description:
      "ไปเที่ยวเกาะหลีเป๊ะ พักผ่อนบนหาดทรายขาว เล่นน้ำทะเลใส ๆ ดำน้ำตื้นชมความสวยงามโลกใต้ทะเล ...",
    photos: [
      "https://img.wongnai.com/p/1600x0/2019/07/31/b3986319d85a4d85baecbacf03a519d8.jpg",
      "https://img.wongnai.com/p/1600x0/2019/07/31/9969a296fe1b452a8e64c2b7cfd58319.jpg",
      "https://img.wongnai.com/p/1600x0/2019/07/31/4edab94617f642d7ac11efd9bccb9863.jpg",
      "https://img.wongnai.com/p/1600x0/2019/07/31/58348f4c3ace47349e80e5930a7a525a.jpg",
    ],
    tags: ["ทะเล", "เกาะ", "สตูล", "ธรรมชาติ"],
    latitude: 6.4901,
    longitude: 99.3015,
    province: "สตูล",
    url: "https://www.wongnai.com/articles/koh-lipe-guide",
  },
  {
    id: 7,
    title: "คู่มือเที่ยวไต้หวันง่าย ๆ ฉบับ 2020 ฉลองต่อวีซ่าฟรี!",
    description:
      "วางแผนเที่ยวไต้หวันปี 2020 กัน! ไปเที่ยวไต้หวันจุใจ ไม่ต้องง้อวีซ่า! ...",
    photos: [
      "https://img.wongnai.com/p/1920x0/2019/07/11/cefadd52a1424562bddf0d8fb4762d38.jpg",
      "https://img.wongnai.com/p/1920x0/2019/07/11/eee5544a928c4766a15a04852135dbbb.jpg",
      "https://img.wongnai.com/p/1920x0/2019/07/11/c725886d14b144868cf368bee07d1441.jpg",
      "https://img.wongnai.com/p/1920x0/2019/07/11/83cda5c3853443c4a1398fb3ae6b31a8.jpg",
    ],
    tags: ["จุดถ่ายรูป", "ต่างประเทศ", "ไต้หวัน", "ธรรมชาติ"],
    latitude: 25.033,
    longitude: 121.5654,
    province: "ไต้หวัน",
    url: "https://www.wongnai.com/articles/taiwan-travel-guide",
  },
  {
    id: 8,
    title: "เที่ยวญี่ปุ่นไปกับภูเขาไฟชื่อดังให้สุดฟินจาก 15 มุมสุดงาม~",
    description:
      "สายถ่ายรูปห้ามพลาด! ลุยเที่ยวญี่ปุ่น ชมภูเขาไฟฟูจิ ...",
    photos: [
      "https://img.wongnai.com/p/1600x0/2019/06/04/371f3bfad9cd46399f30c8b04c816597.jpg",
      "https://img.wongnai.com/p/1600x0/2019/06/04/93b7b555b624490680ebe7b0a16b0519.jpg",
      "https://img.wongnai.com/p/1600x0/2019/06/04/bf2c001a72314452a261bff3f039ccdc.jpg",
      "https://img.wongnai.com/p/1600x0/2019/06/04/37b377e43f344da794cb3353aeca3fcf.jpg",
    ],
    tags: ["จุดถ่ายรูป", "ต่างประเทศ", "ญี่ปุ่น", "ธรรมชาติ", "ภูเขา"],
    latitude: 35.3606,
    longitude: 138.7274,
    province: "ญี่ปุ่น",
    url: "https://www.wongnai.com/articles/mt-fuji-15-views",
  },
  {
    id: 9,
    title:
      "เที่ยวฝรั่งเศส เช็กอินเมืองในฝัน เก็บแลนด์มาร์กสุดปังในปารีส",
    description:
      "1 ใน Dream Destination ของใครหลาย ๆ คนคงหนีไม่พ้นการเที่ยวฝรั่งเศส เพราะที่มีทั้งหอไอเฟล พิพิธภัณฑ์ลูฟวร์ และ Disneyland ...",
    photos: [
      "https://img.wongnai.com/p/1600x0/2019/05/23/4057d08671744f8e8fd9e939b5e60087.jpg",
      "https://img.wongnai.com/p/1600x0/2019/05/23/0176c0c06f2449849e826844733ef1e4.jpg",
      "https://img.wongnai.com/p/1600x0/2019/05/23/0e56670f9e0e43e7b26c312583ecca51.jpg",
      "https://img.wongnai.com/p/1600x0/2019/05/23/7a677ee703ab448b9ca20c847aa74ce3.jpg",
    ],
    tags: ["จุดถ่ายรูป", "ต่างประเทศ", "ฝรั่งเศส"],
    latitude: 48.8566,
    longitude: 2.3522,
    province: "ฝรั่งเศส",
    url: "https://www.wongnai.com/articles/paris-france-guide",
  },
  {
    id: 10,
    title:
      "เที่ยวฟินแลนด์แบบฟิน ๆ ไปฟาร์ม Husky ขี่ Reindeer เล่น Snowmobile",
    description:
      "ใครว่าเที่ยวฟินแลนด์ มีแค่ล่าแสงเหนือ กิจกรรมกลางภูเขาหิมะเค้าก็มีให้ทำเพียบ! ทั้งขี่ Husky, Reindeer ขับเจ็ทสกีหิมะท่ามกลางอุณภูมิติดลบ ...",
    photos: [
      "https://img.wongnai.com/p/1600x0/2019/05/21/cd54e9c640694a95b7e0103b0ead5408.jpg",
      "https://img.wongnai.com/p/1600x0/2019/05/21/cf792ff02ab0491b9f30d67d33634f78.jpg",
      "https://img.wongnai.com/p/1600x0/2019/05/21/e87ce6e73c634532afd50dd02517fee9.jpg",
      "https://img.wongnai.com/p/1600x0/2019/05/21/ea0f93584b3c4f3ba0680fcc11bc3a13.jpg",
    ],
    tags: ["ต่างประเทศ", "ฟินแลนด์", "หิมะ", "ธรรมชาติ"],
    latitude: 66.5039,
    longitude: 25.7294,
    province: "ฟินแลนด์",
    url: "https://www.wongnai.com/articles/finland-snow-activities",
  },
];

/**
 * ดึงลิสต์ trips ทั้งหมด (พร้อม search keyword แบบง่ายๆ)
 */
export async function getTrips(keyword = ""): Promise<Trip[]> {
  const kw = keyword.trim().toLowerCase();

  if (!kw) {
    return Promise.resolve(MOCK_TRIPS);
  }

  const filtered = MOCK_TRIPS.filter((trip) => {
    const haystack =
      (trip.title ?? "") +
      " " +
      (trip.description ?? "") +
      " " +
      (trip.province ?? "") +
      " " +
      (trip.tags ?? []).join(" ");

    return haystack.toLowerCase().includes(kw);
  });

  return Promise.resolve(filtered);
}

/**
 * ดึง trip ตาม id (ใช้กับหน้า /trips/:id)
 */
export async function getTripById(id: number): Promise<Trip> {
  const trip = MOCK_TRIPS.find((t) => t.id === id);
  if (!trip) {
    return Promise.reject(new Error("Trip not found"));
  }
  return Promise.resolve(trip);
}