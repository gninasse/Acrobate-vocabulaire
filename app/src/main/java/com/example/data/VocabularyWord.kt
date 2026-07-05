package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary_words")
data class VocabularyWord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lotId: Int, // Lot 1 to 10
    val french: String,
    val meaning: String,
    val mentalImage: String,
    val exampleFr: String,
    val exampleTr: String,
    val status: String = "NEW", // "NEW", "LEARNING", "MASTERED"
    val leitnerBox: Int = 1, // Box 1 to 5
    val nextReviewTime: Long = 0L, // Timestamp in ms
    val lastReviewedTime: Long = 0L,
    val successfulRecalls: Int = 0, // Number of consecutive successful recalls
    val totalAttempts: Int = 0
) {
    val isReadyForReview: Boolean
        get() = status == "NEW" || nextReviewTime <= System.currentTimeMillis()
}

object InitialVocabulary {
    fun getWords(): List<VocabularyWord> {
        return listOf(
            // --- LOT 1 : Salutations et premiers besoins ---
            VocabularyWord(
                lotId = 1,
                french = "Bonjour",
                meaning = "Hello / Good morning",
                mentalImage = "Un grand soleil jaune qui sourit et fait un signe de la main.",
                exampleFr = "Bonjour maman, bien dormi ?",
                exampleTr = "Good morning mom, did you sleep well?"
            ),
            VocabularyWord(
                lotId = 1,
                french = "Merci",
                meaning = "Thank you",
                mentalImage = "Un enfant qui tend une jolie fleur avec un grand sourire.",
                exampleFr = "Merci pour ce délicieux gâteau !",
                exampleTr = "Thank you for this delicious cake!"
            ),
            VocabularyWord(
                lotId = 1,
                french = "Oui",
                meaning = "Yes",
                mentalImage = "Une tête rigolote qui fait de grands mouvements de haut en bas.",
                exampleFr = "Oui, je veux bien jouer avec toi !",
                exampleTr = "Yes, I would love to play with you!"
            ),
            VocabularyWord(
                lotId = 1,
                french = "Non",
                meaning = "No",
                mentalImage = "Un petit panneau rouge amusé qui dit non de gauche à droite.",
                exampleFr = "Non, je n'ai pas froid.",
                exampleTr = "No, I am not cold."
            ),
            VocabularyWord(
                lotId = 1,
                french = "S'il vous plaît",
                meaning = "Please",
                mentalImage = "Deux petites mains jointes pour demander poliment.",
                exampleFr = "Un verre d'eau, s'il vous plaît.",
                exampleTr = "A glass of water, please."
            ),
            VocabularyWord(
                lotId = 1,
                french = "L'eau",
                meaning = "Water",
                mentalImage = "Un verre transparent rempli d'eau fraîche avec des bulles qui pétillent.",
                exampleFr = "Je bois de l'eau bien fraîche.",
                exampleTr = "I am drinking nice fresh water."
            ),
            VocabularyWord(
                lotId = 1,
                french = "Le pain",
                meaning = "Bread",
                mentalImage = "Une longue baguette dorée et chaude qui croustille.",
                exampleFr = "Je mange du pain au petit-déjeuner.",
                exampleTr = "I eat bread for breakfast."
            ),
            VocabularyWord(
                lotId = 1,
                french = "Manger",
                meaning = "To eat",
                mentalImage = "Une pomme croquée par une bouche gourmande.",
                exampleFr = "J'aime manger des fruits mûrs.",
                exampleTr = "I like to eat ripe fruits."
            ),
            VocabularyWord(
                lotId = 1,
                french = "Boire",
                meaning = "To drink",
                mentalImage = "Une paille rayée plongée dans un jus de fruits coloré.",
                exampleFr = "Tu veux boire un jus d'orange ?",
                exampleTr = "Do you want to drink an orange juice?"
            ),
            VocabularyWord(
                lotId = 1,
                french = "Toilettes",
                meaning = "Toilets / Bathroom",
                mentalImage = "Une porte blanche avec un petit bonhomme amusant dessus.",
                exampleFr = "Où sont les toilettes, s'il vous plaît ?",
                exampleTr = "Where is the bathroom, please?"
            ),

            // --- LOT 2 : Famille et Sentiments ---
            VocabularyWord(
                lotId = 2,
                french = "Maman",
                meaning = "Mom",
                mentalImage = "Une maman douce qui ouvre grand ses bras pour faire un câlin.",
                exampleFr = "Maman, je t'aime de tout mon cœur.",
                exampleTr = "Mom, I love you with all my heart."
            ),
            VocabularyWord(
                lotId = 2,
                french = "Papa",
                meaning = "Dad",
                mentalImage = "Un papa souriant qui porte son enfant sur ses épaules.",
                exampleFr = "Papa joue au ballon avec moi.",
                exampleTr = "Dad plays ball with me."
            ),
            VocabularyWord(
                lotId = 2,
                french = "Le frère",
                meaning = "Brother",
                mentalImage = "Deux garçons complices qui font une cabane en carton.",
                exampleFr = "Mon frère est très gentil avec moi.",
                exampleTr = "My brother is very kind to me."
            ),
            VocabularyWord(
                lotId = 2,
                french = "La sœur",
                meaning = "Sister",
                mentalImage = "Une fille joyeuse qui fait une grimace rigolote.",
                exampleFr = "Ma sœur adore dessiner des fleurs.",
                exampleTr = "My sister loves to draw flowers."
            ),
            VocabularyWord(
                lotId = 2,
                french = "L'ami",
                meaning = "Friend",
                mentalImage = "Deux enfants qui marchent main dans la main dans la cour.",
                exampleFr = "Thomas est mon meilleur ami.",
                exampleTr = "Thomas is my best friend."
            ),
            VocabularyWord(
                lotId = 2,
                french = "Content",
                meaning = "Happy",
                mentalImage = "Un smiley jaune qui sourit jusqu'aux oreilles sous un grand soleil.",
                exampleFr = "Je suis content quand je vais au parc.",
                exampleTr = "I am happy when I go to the park."
            ),
            VocabularyWord(
                lotId = 2,
                french = "Triste",
                meaning = "Sad",
                mentalImage = "Un petit nuage gris qui pleut doucement une petite larme.",
                exampleFr = "Le petit garçon est triste car il a perdu son jouet.",
                exampleTr = "The little boy is sad because he lost his toy."
            ),
            VocabularyWord(
                lotId = 2,
                french = "Fatigué",
                meaning = "Tired",
                mentalImage = "Un petit chat tout mignon qui baille très fort sous sa couverture.",
                exampleFr = "Je suis fatigué, je vais aller au lit.",
                exampleTr = "I am tired, I am going to go to bed."
            ),
            VocabularyWord(
                lotId = 2,
                french = "Malade",
                meaning = "Sick",
                mentalImage = "Un nounours avec un thermomètre sous le bras et un pansement.",
                exampleFr = "Aujourd'hui, j'ai mal à la gorge, je suis malade.",
                exampleTr = "Today, I have a sore throat, I am sick."
            ),
            VocabularyWord(
                lotId = 2,
                french = "La peur",
                meaning = "Fear / Scared",
                mentalImage = "Un petit monstre tout poilu caché sous le lit avec de grands yeux drôles.",
                exampleFr = "Je n'ai pas peur du noir !",
                exampleTr = "I am not afraid of the dark!"
            ),

            // --- LOT 3 : Actions de base ---
            VocabularyWord(
                lotId = 3,
                french = "Aller",
                meaning = "To go",
                mentalImage = "Une flèche verte dynamique qui pointe vers une belle maison.",
                exampleFr = "Je veux aller au parc pour jouer.",
                exampleTr = "I want to go to the park to play."
            ),
            VocabularyWord(
                lotId = 3,
                french = "Venir",
                meaning = "To come",
                mentalImage = "Une main amicale qui fait signe d'approcher.",
                exampleFr = "Viens voir ce magnifique papillon !",
                exampleTr = "Come see this beautiful butterfly!"
            ),
            VocabularyWord(
                lotId = 3,
                french = "Jouer",
                meaning = "To play",
                mentalImage = "Des cubes de toutes les couleurs empilés en forme de château.",
                exampleFr = "On va jouer ensemble au ballon ?",
                exampleTr = "Shall we play ball together?"
            ),
            VocabularyWord(
                lotId = 3,
                french = "Dormir",
                meaning = "To sleep",
                mentalImage = "Une jolie lune souriante entourée de petites étoiles dorées.",
                exampleFr = "Chut ! Le bébé va dormir.",
                exampleTr = "Shh! The baby is going to sleep."
            ),
            VocabularyWord(
                lotId = 3,
                french = "Regarder",
                meaning = "To look / watch",
                mentalImage = "Une grande paire de jumelles magiques tournée vers un oiseau.",
                exampleFr = "Regarde ce bel arc-en-ciel dehors !",
                exampleTr = "Look at this beautiful rainbow outside!"
            ),
            VocabularyWord(
                lotId = 3,
                french = "Écouter",
                meaning = "To listen",
                mentalImage = "Une oreille amusante qui capte de petites notes de musique colorées.",
                exampleFr = "Écoute le joli chant des oiseaux !",
                exampleTr = "Listen to the sweet singing of the birds!"
            ),
            VocabularyWord(
                lotId = 3,
                french = "Parler",
                meaning = "To speak / talk",
                mentalImage = "Deux bulles de BD de couleurs différentes qui se croisent joyeusement.",
                exampleFr = "J'aime parler français avec mes amis.",
                exampleTr = "I like speaking French with my friends."
            ),
            VocabularyWord(
                lotId = 3,
                french = "Prendre",
                meaning = "To take",
                mentalImage = "Une main d'enfant qui attrape doucement un bonbon.",
                exampleFr = "Je vais prendre mon sac à dos pour l'école.",
                exampleTr = "I am going to take my backpack for school."
            ),
            VocabularyWord(
                lotId = 3,
                french = "Donner",
                meaning = "To give",
                mentalImage = "Une main ouverte qui offre un joli paquet cadeau entouré d'un ruban.",
                exampleFr = "Peux-tu me donner ce stylo rouge ?",
                exampleTr = "Can you give me that red pen?"
            ),
            VocabularyWord(
                lotId = 3,
                french = "Aimer",
                meaning = "To love / like",
                mentalImage = "Un magnifique cœur rouge tout chaud qui brille joyeusement.",
                exampleFr = "J'adore aimer mes petits animaux de compagnie.",
                exampleTr = "I love loving my little pets."
            ),

            // --- LOT 4 : Animaux et Nature ---
            VocabularyWord(
                lotId = 4,
                french = "Le chien",
                meaning = "Dog",
                mentalImage = "Un petit chiot qui remue joyeusement sa queue en courant après sa balle.",
                exampleFr = "Le chien aboie quand le facteur arrive.",
                exampleTr = "The dog barks when the mail carrier arrives."
            ),
            VocabularyWord(
                lotId = 4,
                french = "Le chat",
                meaning = "Cat",
                mentalImage = "Un chaton tout roux qui ronronne doucement endormi sur un coussin.",
                exampleFr = "Le chat boit du bon lait frais.",
                exampleTr = "The cat drinks good fresh milk."
            ),
            VocabularyWord(
                lotId = 4,
                french = "L'oiseau",
                meaning = "Bird",
                mentalImage = "Un petit oiseau bleu perché sur une branche chantant une mélodie.",
                exampleFr = "L'oiseau s'envole très haut dans le ciel.",
                exampleTr = "The bird flies very high in the sky."
            ),
            VocabularyWord(
                lotId = 4,
                french = "La maison",
                meaning = "House / Home",
                mentalImage = "Une maison avec un toit rouge, deux fenêtres carrées et une cheminée fumante.",
                exampleFr = "C'est ma maison, j'y habite avec ma famille.",
                exampleTr = "This is my house, I live here with my family."
            ),
            VocabularyWord(
                lotId = 4,
                french = "Le jardin",
                meaning = "Garden",
                mentalImage = "Un carré de pelouse verte avec des fleurs multicolores et des papillons.",
                exampleFr = "Nous jouons à cache-cache dans le jardin.",
                exampleTr = "We play hide-and-seek in the garden."
            ),
            VocabularyWord(
                lotId = 4,
                french = "L'arbre",
                meaning = "Tree",
                mentalImage = "Un grand chêne bien vert avec de grosses branches solides pour grimper.",
                exampleFr = "Le chat monte tout en haut de l'arbre.",
                exampleTr = "The cat climbs to the very top of the tree."
            ),
            VocabularyWord(
                lotId = 4,
                french = "Le soleil",
                meaning = "Sun",
                mentalImage = "Un cercle d'or brillant de mille feux dans un ciel bleu.",
                exampleFr = "Le soleil brille et réchauffe ma peau.",
                exampleTr = "The sun shines and warms up my skin."
            ),
            VocabularyWord(
                lotId = 4,
                french = "La pluie",
                meaning = "Rain",
                mentalImage = "Des petites gouttes d'eau transparentes qui tapent contre un parapluie violet.",
                exampleFr = "J'aime courir sous la pluie avec mes bottes.",
                exampleTr = "I like running in the rain with my boots."
            ),
            VocabularyWord(
                lotId = 4,
                french = "La fleur",
                meaning = "Flower",
                mentalImage = "Une tulipe rouge vif avec deux grandes feuilles vertes.",
                exampleFr = "J'offre une belle fleur jaune à ma maman.",
                exampleTr = "I offer a beautiful yellow flower to my mom."
            ),
            VocabularyWord(
                lotId = 4,
                french = "Le ciel",
                meaning = "Sky",
                mentalImage = "Un immense drap bleu avec des petits nuages cotonneux blancs.",
                exampleFr = "Regarde les avions passer dans le ciel.",
                exampleTr = "Watch the airplanes pass in the sky."
            ),

            // --- LOT 5 : Couleurs et Nombres ---
            VocabularyWord(
                lotId = 5,
                french = "Rouge",
                meaning = "Red",
                mentalImage = "Une fraise bien mûre et sucrée qui brille sous la feuille.",
                exampleFr = "Ma couleur préférée est le rouge.",
                exampleTr = "My favorite color is red."
            ),
            VocabularyWord(
                lotId = 5,
                french = "Bleu",
                meaning = "Blue",
                mentalImage = "L'immensité de l'océan avec un petit poisson bleu.",
                exampleFr = "J'ai un ballon bleu très grand.",
                exampleTr = "I have a very big blue balloon."
            ),
            VocabularyWord(
                lotId = 5,
                french = "Jaune",
                meaning = "Yellow",
                mentalImage = "Un petit caneton tout mignon qui nage dans l'eau.",
                exampleFr = "Le citron est jaune et très acide !",
                exampleTr = "The lemon is yellow and very sour!"
            ),
            VocabularyWord(
                lotId = 5,
                french = "Vert",
                meaning = "Green",
                mentalImage = "Une petite grenouille joyeuse assise sur une feuille de nénuphar.",
                exampleFr = "L'herbe du jardin est bien verte au printemps.",
                exampleTr = "The grass in the garden is very green in spring."
            ),
            VocabularyWord(
                lotId = 5,
                french = "Noir",
                meaning = "Black",
                mentalImage = "Un chat noir mystérieux aux yeux dorés clignotants.",
                exampleFr = "Le ciel est noir pendant la nuit.",
                exampleTr = "The sky is black during the night."
            ),
            VocabularyWord(
                lotId = 5,
                french = "Blanc",
                meaning = "White",
                mentalImage = "Un magnifique bonhomme de neige bien rond avec une carotte pour nez.",
                exampleFr = "Le lait frais est d'un blanc pur.",
                exampleTr = "The fresh milk is a pure white."
            ),
            VocabularyWord(
                lotId = 5,
                french = "Un",
                meaning = "One",
                mentalImage = "Un doigt levé fièrement pour compter un objet.",
                exampleFr = "J'ai un petit chien dans ma maison.",
                exampleTr = "I have one small dog in my house."
            ),
            VocabularyWord(
                lotId = 5,
                french = "Deux",
                meaning = "Two",
                mentalImage = "Deux petites cerises bien rouges attachées par leur queue verte.",
                exampleFr = "Je mange deux biscuits au chocolat.",
                exampleTr = "I eat two chocolate biscuits."
            ),
            VocabularyWord(
                lotId = 5,
                french = "Trois",
                meaning = "Three",
                mentalImage = "Trois ballons de baudruche multicolores qui s'envolent dans l'air.",
                exampleFr = "Il y a trois oiseaux sur le toit.",
                exampleTr = "There are three birds on the roof."
            ),
            VocabularyWord(
                lotId = 5,
                french = "Beaucoup",
                meaning = "A lot / Many",
                mentalImage = "Une montagne géante de bonbons multicolores.",
                exampleFr = "J'ai beaucoup d'amis à l'école.",
                exampleTr = "I have a lot of friends at school."
            ),

            // --- LOT 6 : École et Objets ---
            VocabularyWord(
                lotId = 6,
                french = "Le livre",
                meaning = "Book",
                mentalImage = "Un grand livre de contes de fées ouvert avec un château magique qui en sort.",
                exampleFr = "Je lis un livre avant de m'endormir.",
                exampleTr = "I read a book before falling asleep."
            ),
            VocabularyWord(
                lotId = 6,
                french = "Le stylo",
                meaning = "Pen",
                mentalImage = "Un stylo bleu magique qui écrit de jolies lettres brillantes.",
                exampleFr = "J'écris mon prénom avec un stylo bleu.",
                exampleTr = "I write my first name with a blue pen."
            ),
            VocabularyWord(
                lotId = 6,
                french = "La table",
                meaning = "Table",
                mentalImage = "Une table en bois solide sur laquelle est posée une délicieuse tarte.",
                exampleFr = "Le cahier est posé sur la table.",
                exampleTr = "The notebook is placed on the table."
            ),
            VocabularyWord(
                lotId = 6,
                french = "La chaise",
                meaning = "Chair",
                mentalImage = "Une petite chaise rouge confortable ajustée pour un enfant.",
                exampleFr = "Je m'assois sur la chaise pour étudier.",
                exampleTr = "I sit on the chair to study."
            ),
            VocabularyWord(
                lotId = 6,
                french = "L'école",
                meaning = "School",
                mentalImage = "Un grand bâtiment joyeux avec une cloche dorée et des drapeaux colorés.",
                exampleFr = "J'aime aller à l'école tous les matins.",
                exampleTr = "I like going to school every morning."
            ),
            VocabularyWord(
                lotId = 6,
                french = "Le sac",
                meaning = "Bag / Backpack",
                mentalImage = "Un joli sac à dos avec des poches et des dessins rigolos.",
                exampleFr = "Je mets mes livres dans mon sac.",
                exampleTr = "I put my books in my bag."
            ),
            VocabularyWord(
                lotId = 6,
                french = "L'image",
                meaning = "Picture / Illustration",
                mentalImage = "Une jolie carte postale avec un dessin coloré d'un petit dauphin.",
                exampleFr = "Cette image de dauphin est magnifique.",
                exampleTr = "This picture of a dolphin is magnificent."
            ),
            VocabularyWord(
                lotId = 6,
                french = "Le jeu",
                meaning = "Game / Toy",
                mentalImage = "Une boîte de jeu de société colorée avec des dés et des pions rigolos.",
                exampleFr = "Ce jeu de cartes est très amusant.",
                exampleTr = "This card game is very fun."
            ),
            VocabularyWord(
                lotId = 6,
                french = "Le papier",
                meaning = "Paper",
                mentalImage = "Une feuille blanche immaculée prête pour recevoir de magnifiques dessins.",
                exampleFr = "Je dessine un beau soleil sur le papier.",
                exampleTr = "I draw a beautiful sun on the paper."
            ),
            VocabularyWord(
                lotId = 6,
                french = "La porte",
                meaning = "Door",
                mentalImage = "Une grande porte en bois avec une poignée dorée qui brille.",
                exampleFr = "Ouvre la porte du jardin, s'il te plaît.",
                exampleTr = "Open the garden door, please."
            ),

            // --- LOT 7 : Nourriture ---
            VocabularyWord(
                lotId = 7,
                french = "La pomme",
                meaning = "Apple",
                mentalImage = "Une belle pomme rouge et ronde bien brillante avec une petite feuille verte sur le dessus.",
                exampleFr = "Je croque dans une bonne pomme.",
                exampleTr = "I bite into a good apple."
            ),
            VocabularyWord(
                lotId = 7,
                french = "Le lait",
                meaning = "Milk",
                mentalImage = "Un grand bol de lait bien blanc avec quelques céréales amusantes.",
                exampleFr = "Je bois un verre de lait bien chaud.",
                exampleTr = "I drink a hot glass of milk."
            ),
            VocabularyWord(
                lotId = 7,
                french = "Le chocolat",
                meaning = "Chocolate",
                mentalImage = "Une tablette de chocolat marron avec quelques carrés déjà cassés.",
                exampleFr = "J'adore manger du chocolat noir.",
                exampleTr = "I love eating dark chocolate."
            ),
            VocabularyWord(
                lotId = 7,
                french = "La banane",
                meaning = "Banana",
                mentalImage = "Une banane jaune incurvée en forme de grand sourire rigolo.",
                exampleFr = "La banane est douce et sucrée.",
                exampleTr = "The banana is sweet and sugary."
            ),
            VocabularyWord(
                lotId = 7,
                french = "Le gâteau",
                meaning = "Cake",
                mentalImage = "Un gros gâteau d'anniversaire au chocolat avec des bougies allumées.",
                exampleFr = "Il souffle les bougies de son gâteau.",
                exampleTr = "He blows out the candles of his cake."
            ),
            VocabularyWord(
                lotId = 7,
                french = "Le fromage",
                meaning = "Cheese",
                mentalImage = "Un morceau de fromage jaune avec des petits trous amusants de souris.",
                exampleFr = "La petite souris adore manger le fromage.",
                exampleTr = "The little mouse loves eating the cheese."
            ),
            VocabularyWord(
                lotId = 7,
                french = "La soupe",
                meaning = "Soup",
                mentalImage = "Un bol de soupe bien chaude d'où s'échappent des petits nuages de vapeur.",
                exampleFr = "Cette soupe de légumes me réchauffe.",
                exampleTr = "This vegetable soup warms me up."
            ),
            VocabularyWord(
                lotId = 7,
                french = "Le jus",
                meaning = "Juice",
                mentalImage = "Une grande carafe en verre remplie d'un jus d'orange ensoleillé.",
                exampleFr = "Je bois un jus de pomme délicieux.",
                exampleTr = "I drink a delicious apple juice."
            ),
            VocabularyWord(
                lotId = 7,
                french = "L'assiette",
                meaning = "Plate",
                mentalImage = "Une assiette blanche et ronde décorée de jolis petits coeurs bleus.",
                exampleFr = "Je pose mes légumes dans l'assiette.",
                exampleTr = "I put my vegetables on the plate."
            ),
            VocabularyWord(
                lotId = 7,
                french = "La cuillère",
                meaning = "Spoon",
                mentalImage = "Une cuillère brillante prête à plonger dans un yaourt crémeux.",
                exampleFr = "Je mange ma soupe avec une cuillère.",
                exampleTr = "I eat my soup with a spoon."
            ),

            // --- LOT 8 : Corps et Vêtements ---
            VocabularyWord(
                lotId = 8,
                french = "La tête",
                meaning = "Head",
                mentalImage = "Une tête ronde souriante qui porte une jolie couronne dorée.",
                exampleFr = "J'ai un joli chapeau sur la tête.",
                exampleTr = "I have a nice hat on my head."
            ),
            VocabularyWord(
                lotId = 8,
                french = "La main",
                meaning = "Hand",
                mentalImage = "Une petite main ouverte avec cinq doigts tendus pour dire bonjour.",
                exampleFr = "Je donne la main à mon papa.",
                exampleTr = "I give my hand to my dad."
            ),
            VocabularyWord(
                lotId = 8,
                french = "Le pied",
                meaning = "Foot",
                mentalImage = "Un pied amusant qui tape dans un grand ballon de foot de toutes les couleurs.",
                exampleFr = "Le ballon se tape avec le pied.",
                exampleTr = "The ball is kicked with the foot."
            ),
            VocabularyWord(
                lotId = 8,
                french = "Les yeux",
                meaning = "Eyes",
                mentalImage = "Deux grands yeux pétillants de curiosité qui regardent le monde.",
                exampleFr = "Mes yeux regardent les belles étoiles.",
                exampleTr = "My eyes look at the beautiful stars."
            ),
            VocabularyWord(
                lotId = 8,
                french = "La jambe",
                meaning = "Leg",
                mentalImage = "Une jambe d'athlète agile qui court et saute par-dessus un petit ruisseau.",
                exampleFr = "J'ai couru très vite et j'ai mal à la jambe.",
                exampleTr = "I ran very fast and my leg hurts."
            ),
            VocabularyWord(
                lotId = 8,
                french = "Le pyjama",
                meaning = "Pajamas",
                mentalImage = "Un pyjama bleu doux avec des petites lunes jaunes imprimées partout.",
                exampleFr = "Je mets mon pyjama avant de dormir.",
                exampleTr = "I put on my pajamas before sleeping."
            ),
            VocabularyWord(
                lotId = 8,
                french = "La chaussure",
                meaning = "Shoe",
                mentalImage = "Une chaussure rouge brillante avec des lacets bien noués en boucle.",
                exampleFr = "Je mets ma chaussure pour sortir dehors.",
                exampleTr = "I put on my shoe to go outside."
            ),
            VocabularyWord(
                lotId = 8,
                french = "Le t-shirt",
                meaning = "T-shirt",
                mentalImage = "Un t-shirt d'été tout jaune avec un dessin de soleil rigolo.",
                exampleFr = "Ce t-shirt blanc est tout propre.",
                exampleTr = "This white t-shirt is completely clean."
            ),
            VocabularyWord(
                lotId = 8,
                french = "Le pantalon",
                meaning = "Pants / Trousers",
                mentalImage = "Un pantalon en jean bleu solide parfait pour jouer dans le jardin.",
                exampleFr = "J'ai fait un trou au genou de mon pantalon.",
                exampleTr = "I made a hole in the knee of my pants."
            ),
            VocabularyWord(
                lotId = 8,
                french = "Le chapeau",
                meaning = "Hat",
                mentalImage = "Un chapeau magique de magicien étoilé d'où sort un petit lapin blanc.",
                exampleFr = "Le magicien porte un grand chapeau noir.",
                exampleTr = "The magician wears a big black hat."
            ),

            // --- LOT 9 : Temps et Lieux ---
            VocabularyWord(
                lotId = 9,
                french = "Le jour",
                meaning = "Day / Daytime",
                mentalImage = "Une belle lumière d'or qui éclaire toute la campagne au réveil.",
                exampleFr = "Il fait très beau aujourd'hui en plein jour.",
                exampleTr = "It is very beautiful today in broad daylight."
            ),
            VocabularyWord(
                lotId = 9,
                french = "La nuit",
                meaning = "Night / Nighttime",
                mentalImage = "Un ciel bleu foncé avec une jolie lune blanche et plein de petites étoiles.",
                exampleFr = "Les hiboux se réveillent pendant la nuit.",
                exampleTr = "Owls wake up during the night."
            ),
            VocabularyWord(
                lotId = 9,
                french = "Le matin",
                meaning = "Morning",
                mentalImage = "Un petit coq rigolo qui chante debout sur une barrière en bois.",
                exampleFr = "Je prends mon bol de céréales le matin.",
                exampleTr = "I have my bowl of cereal in the morning."
            ),
            VocabularyWord(
                lotId = 9,
                french = "Le soir",
                meaning = "Evening / Night",
                mentalImage = "Un magnifique coucher de soleil orange derrière les petites collines.",
                exampleFr = "Nous mangeons tous ensemble le soir.",
                exampleTr = "We eat all together in the evening."
            ),
            VocabularyWord(
                lotId = 9,
                french = "Le lit",
                meaning = "Bed",
                mentalImage = "Un lit douillet avec un oreiller moelleux blanc et un doudou ours.",
                exampleFr = "Je saute dans mon lit tout doux.",
                exampleTr = "I jump into my very soft bed."
            ),
            VocabularyWord(
                lotId = 9,
                french = "La chambre",
                meaning = "Bedroom",
                mentalImage = "Une jolie chambre décorée avec des jouets et des dessins aux murs.",
                exampleFr = "Je range tous mes jouets dans ma chambre.",
                exampleTr = "I tidy up all my toys in my bedroom."
            ),
            VocabularyWord(
                lotId = 9,
                french = "La cuisine",
                meaning = "Kitchen",
                mentalImage = "Une grande pièce chaude avec de bonnes odeurs de soupe et de crêpes.",
                exampleFr = "Maman prépare des gâteaux dans la cuisine.",
                exampleTr = "Mom is preparing cakes in the kitchen."
            ),
            VocabularyWord(
                lotId = 9,
                french = "La ville",
                meaning = "City / Town",
                mentalImage = "Des jolis immeubles colorés avec des petites voitures et des lampadaires.",
                exampleFr = "Il y a de grands magasins dans la ville.",
                exampleTr = "There are big shops in the city."
            ),
            VocabularyWord(
                lotId = 9,
                french = "Le parc",
                meaning = "Park / Playground",
                mentalImage = "Un toboggan rouge et une balançoire verte installés sur de la pelouse.",
                exampleFr = "Je glisse sur le grand toboggan du parc.",
                exampleTr = "I slide down the big slide in the park."
            ),
            VocabularyWord(
                lotId = 9,
                french = "La voiture",
                meaning = "Car",
                mentalImage = "Une petite voiture rouge de dessin animé qui roule joyeusement.",
                exampleFr = "Mon papa conduit une belle voiture rouge.",
                exampleTr = "My dad drives a beautiful red car."
            ),

            // --- LOT 10 : Descriptions de base ---
            VocabularyWord(
                lotId = 10,
                french = "Petit",
                meaning = "Small / Little",
                mentalImage = "Une toute petite coccinelle rouge posée sur le bout de l'index.",
                exampleFr = "Regarde ce petit oiseau, il est mignon.",
                exampleTr = "Look at this small bird, it is cute."
            ),
            VocabularyWord(
                lotId = 10,
                french = "Grand",
                meaning = "Big / Tall",
                mentalImage = "Une girafe géante au long cou souriant au-dessus des nuages.",
                exampleFr = "Mon grand frère est très fort en sport.",
                exampleTr = "My big brother is very strong in sports."
            ),
            VocabularyWord(
                lotId = 10,
                french = "Chaud",
                meaning = "Hot / Warm",
                mentalImage = "Un feu de camp crépitant ou une tasse de chocolat brûlant.",
                exampleFr = "Fais attention, ce bol de soupe est bien chaud.",
                exampleTr = "Be careful, this bowl of soup is very hot."
            ),
            VocabularyWord(
                lotId = 10,
                french = "Froid",
                meaning = "Cold",
                mentalImage = "Un glaçon transparent ou un ours polaire avec un cache-nez.",
                exampleFr = "L'eau du ruisseau est glacée et très froide.",
                exampleTr = "The water of the stream is icy and very cold."
            ),
            VocabularyWord(
                lotId = 10,
                french = "Beau",
                meaning = "Beautiful",
                mentalImage = "Un papillon aux mille couleurs étincelantes sous la lumière.",
                exampleFr = "C'est un beau château de sable doré.",
                exampleTr = "It is a beautiful golden sandcastle."
            ),
            VocabularyWord(
                lotId = 10,
                french = "Bon",
                meaning = "Good / Delicious",
                mentalImage = "Un enfant qui mange une pomme juteuse en se léchant les babines.",
                exampleFr = "Ce gâteau fait maison est vraiment bon !",
                exampleTr = "This homemade cake is really good!"
            ),
            VocabularyWord(
                lotId = 10,
                french = "Facile",
                meaning = "Easy",
                mentalImage = "Une petite opération mathématique amusante comme un puzzle de deux pièces.",
                exampleFr = "Ce puzzle de dinosaure est très facile !",
                exampleTr = "This dinosaur puzzle is very easy!"
            ),
            VocabularyWord(
                lotId = 10,
                french = "Difficile",
                meaning = "Difficult / Hard",
                mentalImage = "Une montagne abrupte avec un petit randonneur qui réfléchit à son chemin.",
                exampleFr = "Ce mot de vocabulaire est un peu difficile à prononcer.",
                exampleTr = "This vocabulary word is a little difficult to pronounce."
            ),
            VocabularyWord(
                lotId = 10,
                french = "Nouveau",
                meaning = "New",
                mentalImage = "Un tout nouveau jouet encore brillant dans sa boîte ouverte.",
                exampleFr = "J'ai un nouveau livre de contes illustré.",
                exampleTr = "I have a new illustrated book of tales."
            ),
            VocabularyWord(
                lotId = 10,
                french = "Vieux",
                meaning = "Old",
                mentalImage = "Un vieux grimoire de magicien avec des pages dorées et cornées.",
                exampleFr = "Mon vieux doudou ours est usé mais je l'adore.",
                exampleTr = "My old teddy bear is worn out but I love it."
            )
        )
    }
}
