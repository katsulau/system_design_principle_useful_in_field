# 要約

## CHAPTER 1 小さくまとめてわかりやすくする

### プログラムの変更が楽になる書き方

* わかりやすい名前を使う
* 長いメソッドは段落に分けて読みやすくする
* 目的ごとに変数を用意する(1つの変数を使いまわして代入、つまり破壊的代入をしないこと)
* メソッドの抽出
* 異なるクラスの重複したコードをなくす
  * ⼿順1 それぞれのクラスで該当するコード部分（段落）を、メソッ
    ドに抽出する
    ⼿順2 2つのクラスに参照関係がある場合：参照する側で抽出した
    メソッド呼び出しを、参照先のオブジェクトのメソッドの呼
    び出しに書き換える
    ⼿順2' 2つのクラスに参照関係がない場合：共通のメソッドの置き
    場所として、別のクラスを新たに作成し、元のクラスで抽出
    したメソッドを移動する
    ⼿順3 元の2つのクラスのメソッド呼び出しを、それぞれ新しいク
    ラスの共通メソッドを利⽤するように書き換える
* 狭い関心ごとに特化したクラスにする(業務の関心ごとに対応したクラスを作成する)


「送料」クラスのように、業務で使われる用語に合わせて、
その用語の関心事に対応するクラスををドメインオブジェクトと呼びます。
アプリケーションの対象領域(ドメイン)の関心事を記述したオブジェクトという意味です。

### 値を扱うための専用クラスを作る

値の種類ごとに専用の型を用意するとコードが安定し、
コードの意図が明確になります。
値を扱うためのクラスを作るやり方を値オブジェクトと呼びます。

### 値オブジェクトは不変にする

値オブジェクトを不変にする方法は次の通り

* インスタンス変数はコンストラクタでオブジェクトの生成時に設定する
* インスタンス変数を変更するメソッド(setterメソッド)を作らない
* 別の値が必要であれば、別のインスタンス(オブジェクト)を作る

このような設計のやり方を完全コンストラクタと呼びます

### 型を使ってコードをわかりやすく安全にする

値オブジェクトを使うことによって、引数の渡し間違いを防ぐことができる。


### コレクション型を扱うコードの整理

#### コレクション型を扱うロジックを専用クラスに閉じ込める

例えば、顧客を扱うクラスCustomerを作成した場合、顧客一覧は
List<Customer>で表現できるが、それも専用クラスとして作成する。

```java
class Customers {
    List<Customer> customers;
    
    void add(Customer customer) { ... }
    
    void removeIfExist(Customer customer) { ... }
    
    int count() { ... }
    
    Customers importantCustomers() { ... }
}
```

このように、全てのロジックをCustomersクラスに閉じ込めておけば、
ロジックを変更する時の影響をCustomersクラスに閉じ込めやすくなります。

なお、Customersクラスでは、List<Customer>以外のインスタンス変数を持たないようにします。

このように、コレクションを一つだけ持つ専用クラスを作るやり方をコレクションオブジェクト
あるいはファーストクラスコレクションと呼びます。

### コレクションの状態を安定させる

下記のような実装は悪い例。
外部で、要素の追加や削除ができてしまうため。
```java
class Customers {
    List<Customer> customers;
    
    List<Customer> getList() {
    return customers;
    }
}
```
コレクションの操作を安定させる方法は３つある。

・コレクション操作のロジックをコレクションオブジェクトに移動す
る
・コレクション操作の結果も同じ型のコレクションオブジェクトとし
て返す
・コレクションを「不変」にして外部に渡す

良い例
```java
class Customers {
    List<Customer> customers;
    
    Customers add(Customer customer) {
    List<Customer> result = new ArrayList<>(customers);
    return new Customers(result.add(customer));
    }
}
```

## CHAPTER 2 場合分けのロジックを整理する

if文やswitch文が増えてくると可読性が悪くなる、、どう解決すれば良い？

### 判断や処理のロジックをメソッドに独立させる

```java
// 悪い例
if(customerType.equals("child")) {
  fee = baseFee * 0.5 ;
}

// 良い例
if(isChild()) {
  fee = childFee();
}
// 判断ロジックの詳細
private Boolean isChild() {
    return customerType.equals("child");
}
// 計算ロジックの詳細
private int childFee() {
    return baseFee * 0.5 ;
}

```

### else句をなくすと条件分岐が単純になる

else句がなくした書き方

```java
Yen fee(){
  if(isChild()) return childFee();
  if(isSenior()) return seniorFee();
  return adultFee();
}
```
else句を使わずに早期リターンするこの書き方をガード節と呼びます。

### 区分ごとのロジックを別クラスに分ける

例えば、顧客の区分ごとに料金計算が異なる場合、
顧客区分ごとに大人クラス、子供クラス、シニアクラスを作り、
区分ごとの名称と料金計算のロジックを分けて記述します。

区分ごとにクラスを使う場合、それぞれの型を意識して使わなくても済むように
するには、インターフェースを使って、異なる型を同じ型として扱うことができる。

インターフェース宣言と、区分ごとの専用クラスを組み合わせて、
区分ごとに異なるクラスのオブジェクトを同じ型として扱う仕組みを多態と呼びます。

### 区分ごとのクラスのインスタンスを生成する

区分ごとのクラスのインスタンスを生成する時には、
if文で場合わけが必要になりそうですが、ここではmapを利用することで
if文を不要とすることが可能です。

```java
class FeeFactory {
  static Map<String,Fee> types;

  static
  {
  types.put( "adult", new AdultFee());
  types.put( "child", new ChildFee() );
  }
  static Fee feeByName(String name) {
      return types.get(name);
  }
}
```

料金区分の名前をキーとしてオブジェクトを生成しておき、
mapとして保持しておけば、料金区分名でオブジェクトを取得できます。

### 列挙型を使えばもっとかんたん

多態には、区分の一覧がわかりにくいという問題があります。
列挙型を使えば、一覧を明示的に記述できます。

(パッケージchpter2のコードを参照)

### 状態遷移をわかりやすく記述する

状態遷移を列挙型とコレクションを使って表現する方法は以下となります。

* ある状態から遷移可能な状態(複数)をSetで宣言する
* 遷移元の状態をキーに、遷移可能な状態のSetをバリューにしたMapを宣言する

参考となるコードは、capter2.state配下に記載。

このようなコードを書くことで、

* あるイベントがその状態で起きて良いイベントか起きてはいけないイベントかの判定
* ある状態で発生しても良いイベントの一覧の表示

が可能になります。

## CHAPTER 3 業務ロジックをわかりやすく整理する

### データとロジックを別のクラスに分けることがわかりにくさを生む

三層アーキテクチャを採⽤しても、データクラスと機能クラ スを分ける⼿続き型の設計のままでは、アプリケーションの修正や拡張
が必要になったときに以下の状況になりがち

* 変更の対象箇所を特定するために、プログラムの広い範囲を調べる
* 1つの変更要求に対して、プログラムのあちこちの修正が必要
* 変更の副作⽤が起きていないことを確認するための⼤量のテスト

* 同じ業務ロジックがあちこちに重複して書かれる
* どこに業務ロジックが書いてあるか⾒通しが悪くなる

クラスはデータとロジックを1つのプログラミング単位にまとめるしく
みです。データをインスタンス変数として持ち、そのインスタンス変数
を使った判断／加⼯／計算のロジックをメソッドに書くのが、クラスの
本来の使い⽅です。

データクラスと機能クラスに分ける設計でも、コードの重複を防ぐ⼯
夫はあります。 しかし、この共通ライブラリ⽅式では、業務ロジックの共通化をそれ
ほど実現できません。コードの重複を防げません。
理由は以下二つ。

* 汎⽤化のために使いにくくなるパターン
* ⽤途別に細分化した、たくさんの共通関数を⽤意
  する


### 業務ロジックをわかりやすく整理するための基本のアプローチ

基本的な方針は以下二つ

* データとロジックを⼀体にして業務ロジックを整理する
* 三層のそれぞれの関⼼事と業務ロジックの分離を徹底する

オブジェクト指向では、データとロジックを1つのクラスにまとめま
す。そして、それぞれのクラスを独⽴したプログラミング単位として開
発し、テストします。
クラスにデータとそのデータを使う判断／加⼯／計算のロジックを⼀
緒に書いておけば、コードの重複をなくせます。**そのクラスを使う側の
クラスに同じロジックを書く必要がなくなるからです。**

**クラス設計で⼤切なことは、使う側のクラスのコードがシンプルにな
るように設計することです。**

そのためには以下の点に気をつけます。

* メソッドをロジックの置き場所にする
* ロジックを、データを持つクラスに移動する
* 使う側のクラスにロジックを書き始めたら設計を⾒直す
* メソッドを短くして、ロジックの移動をやりやすくする
* メソッドでは必ずインスタンス変数を使う
* クラスが肥⼤化したら⼩さく分ける
* パッケージを使ってクラスを整理する


### メソッドでは必ずインスタンス変数を使う

インスタンス変数を使わないメソッドは、どこに何が書いてあるかを
わかりにくくします。そのクラスに、そのメソッドを書いている理由が
はっきりしないからです。 どこに何が書いてあるのか推測しやすくするためには、データの近く
にロジックを置く原則を徹底します。

### クラスが肥大化したら小さく分ける

肥大化したクラス
```java
class Customer {
  String firstName;
  String lastName;
  String postalCode;
  String city;
  String address;
  String telephone;
  String mailAddress;
  boolean telephoneNotPreferred;

  String fullName() {
    return String.format("%s %s", firstName, lastName);
  }
}
```

メソッドが全てのインスタンス変数を使うクラスに分ける
```java
class PersonName {
  private String firstName;
  private String lastName;
  String fullName() {
  return String.format("%s %s", firstName, lastName);
  }
}
```

同じようにい、インスタンス変数とメソッドの関係に注目してクラスを小さく分けた結果
```java
class Customer {
  PersonName personName;
  Address address;
  ContactMethod contactMethod;
}
```

このように、関連性の強いデータとロジックだけを集めたクラスを凝
集度が⾼いと⾔います。


### 業務ロジックを小さなオブジェクトに分けて記述する


業務データを使った判断／加⼯／計算の業務ロジックです。オブジェクト指向で業務アプリケーションを開
発する⽬的は、業務ロジックがどこに書いてあるか⾒つけやすくし、修
正を楽で安全にすることです。

関連する業務データと業務ロジックを1つにまとめたこのようなオブジ
ェクトをドメインオブジェクトと呼びます。

ドメインオブジェクトは、業務で扱うデータをインスタンス変数とし
て持ち、その業務データを使った判断／加⼯／計算の業務ロジックを持
つオブジェクトです。

#### ドメインオブジェクトの作り方(概要)

小さな単位に分けて整理する。
* 受注⽇と今⽇の⽇付から受注⽇の妥当性を判断するロジック(OrderAcceptDateクラス)
* 単価と数量から合計価格を計算するロジック(UnitPriceクラス)
* 数値データの価格を千円単位の⽂字列表記に加⼯するロジック(Amountクラス)

例えば、注文を扱いたい場合、注文クラスはロジックの単位としては大きすぎます。
商品、数量、金額、納期、届け先、請求先という単位に分けながらドメインオブジェクトを作っていきます。

それらを組み合わせて注文オブジェクトを組み立てます。
なお、ドメインモデルの設計の仕方は詳しくはCHAPTER 4にまとめてます。

### 業務ロジックの全体を俯瞰して整理する

業務アプリケーションの対象領域（ドメイン）をオブジェクトのモデルとして整理したものをドメインモデルと呼びます。
ドメインモデルは、業務で扱うデータと関連する業務ロジックを集め
て整理したものです。ドメインモデルを⾒れば、業務全体がどういう関
⼼事から成り⽴っているかを理解できます。


### 三層 + ドメインモデルで関心ごとをわかりやすく分離する

三層＋ドメインモデルの構造では、業務ロジックを記述するのはドメ
インモデルだけです。業務的な判断／加⼯／計算のロジックは、すべ
て、ドメインモデルを構成するドメインオブジェクトに任せます。

ドメインモデル⽅式の三層構造では、すべての業務ロジ ックをドメインモデルに集めます。プレゼンテーション層／アプリケー
ション層／データソース層のクラスは、業務上の判断／加⼯／計算のロ
ジックをドメインオブジェクトに任せることで、記述がシンプルになり
役割が明確になります。

## CHAPTER 4 ドメインモデルの考え方で設計する

### ドメインモデルで設計すると何が良いのか

* 業務的な判断／加⼯／計算のロジックを重複なく⼀元的に記述する
* 業務の関⼼事とコードを直接対応させ、どこに何が書いてあるかわ
かりやすく整理する
* 業務ルールの変更や追加のときに、変更の影響を狭い範囲に閉じ込
める

### 利用者の関心事とプログラミング単位を一致させる

ドメインモデルを開発するためには2つの活動が必要です。
* 分析…⼈間のやりたいことを正しく理解する
* 設計…⼈間のやりたいことを動くソフトウェアとして実現する⽅法
を考える


### 分析クラスと設計クラスを一致させる

オブジェクト指向で開発しているつもりなのに、ソフトウェアの変更
がやっかいになる理由のひとつが、分析クラスと設計クラスを別々に考
えてしまうことです。

分析段階では、物事のいろいろな説明ができます。そのいろいろな説
明の中から、業務ロジックをプログラムとして、うまく記述できる設計
クラスを⾒つけることがオブジェクト指向の分析設計なのです。

### 業務に使っている用語をクラス名にする


1. ドメインモデルの設計は、業務で使われる具体的な⽤語（概念）を⼿がかりに進めます。
2. その⽤語が、データとロジックをひとかたまりとしたプログラミング単位として使えそうなことを検証します。

業務の関⼼事、業務で使われている⽤語を理解しながら、プログラムの構造を考えていくのが、オブジェクト指向の分析設計のやり⽅で
す。また、ドメインオブジェクトの設計の基本は、現実の業務の中で使われている具体的な⾔葉の単位で業務ロジックを整理することです。

### データモデルではなくオブジェクトモデル

ドメインモデルの設計に取り組むときに、ドメインモデルとデータモ
デルを別のものとして考えることが⼤切。

**ドメインモデルは、業務ロジックの整理の⼿法です。**
業務データを判断／加⼯／計算するための業務ロジックを、
データとひとまとまりにして「クラス」という単位で整理するのがオブジェクト指向の考え⽅です。
**関⼼の中⼼は業務ロジックであり、データではありません。**

### なぜドメインモデルだと複雑な業務ロジックを整理しやすいのか

業務の関⼼事ごとに対応するクラスを作成し、その関⼼事に関連するデータとロジックをそのクラスに集
めて整理することを繰り返すことで、さまざまな業務ロジックの置き場
所が明確になります。

⼀⽅、データモデルでは、⽣年⽉⽇をもとに年齢を計算したり年齢別の業務ルールを適⽤するロジックを書く場所があいまいです。

### ドメインモデルをどうやって作っていくか

### 部分を作りながら全体を組み立てていく

オブジェクト指向は、部分に注⽬します。個々の部品を作り始め、それを組み合わせながら、段階的に全体を作っていきま
す。ボトムアップのアプローチです。
データとロジックを⼀緒に考え、そのままクラスとして設計することが簡単になるからです。

### 全体と部分を行ったり来たりしながら作っていく

全体を俯瞰する道具としては以下の2つがあります。
* パッケージ図
* 業務フロー図

### 重要な部分から作っていく

全体を俯瞰したら、今度は重要な部分を探します。
重要な部分が業務的にわかりにくい場合は、 
あまり重要でなさそうな部分をいったん除外しながら、重要な部分の候補を⾒つけていきます。
**重要な部分とは、まちがいなく必要になる部分です。**


### 独立した部品を組み合わせて機能を実現する

ドメインオブジェクトを組み合わせて、業務の機能を実現するのは第5章で説明するアプリケーション層のクラスの役割です。
業務で扱うデータとそれを使った判断／加⼯／計算するロジックを⼩さな単位に整理することです。


### ドメインオブジェクトを機能の一部として設計しない

機能を分解しながらプログラム部品を作っていくと、⼀つひとつの部品は、機能の分解構造に依存します。つまり、上位の機能部品と、それを分解して定義した下
位の機能部品はかんたんに切り離せなくなります。

### ドメインオブジェクトの見つけ方

### 重要な関心事や関係性に着目する

業務の重要な関⼼事とそれほど重要でない関⼼事を区別して、重要な関⼼事から⼿を付けていきます。

重要な点を見つけるには。。。？
→ 業務の関心ごとを次に説明するやり方で分類してみる

### 業務の関心事を分類してみる

業務の関⼼事をヒト／モノ／コトの3つに分類する⽅法があります。

ヒト:　個人、企業、担当者など
モノ: 商品、サービス、店舗、場所など
コト: 予約、注文、支払、出荷など

コトの基本属性
* 対象・・・何についての発生した事象か
* 種別・・・どういう種類の事象か
* 時点・・・いつ起きた事象か


### コトに注目すると全体の関係を整理しやすい

コトに注目することで次の関係も明らかになります。

* コトはヒトとモノとの関係として出現する（だれの何についての⾏動か）
* コトは時間軸に沿って明確な前後関係を持つ

### コトは業務ルールの宝庫

受注というコトが発⽣したときには、内容が妥当であることを確認しなければいけません。
・在庫はあるか（出荷可能か）
・与信限度額を超えていないか

などなど、、、

注⽂数量や受注⾦額についての判断／加⼯／計算の業務ロジックが必要です。
このデータとロジックの置き場所がドメインオブジェクトです。
ドメインモデルの設計のアプローチは、まず部品を特定し、その部品ごとに独⽴したクラスを設計することです。


### 期待されるコト、期待されていないコト

約束どおりに実⾏されなかったことの検知には、以下が必要です。
* 予定を記録する
* 実績を記録する
* 差異を判定する


約束したこと（予定）を記録し、実⾏したこと（実績）を記録します。 
そして、適切なタイミングで、予定と実績の差異を算出します。
このような業務のルールを実現するためのクラスの候補は、予定クラ
ス、実績クラス、差異クラスなどです。

業務アプリケーションにどこまで実装するかは別として、以下の点は
⼼に留めておくと、業務ルールの発⾒や理解に役に⽴ちます。

* 業務では必ず想定外のことが起きる
* 想定外のコトが起きたときに、どう対応するかの原則がある

### 業務ルールの記述 ~手続き型とオブジェクト指向の違い

プログラムを書く視点からは、業務ルールの実体は以下の判断ロジックです。

* 数値の⼀致や⼤⼩⽐較
* ⽇付の⼀致や前後⽐較
* ⽂字列の⼀致／不⼀致の判定

### 業務ルールを記述するドメインオブジェクトの基本パターン

ドメインオブジェクトの基本の設計パターン

* 値オブジェクト
* コレクションオブジェクト
* 区分オブジェクト
* 列挙型の集合操作

業務の関心事のパターン

* 口座(Account)パターン・・・現在の値(現在高)を表現し、妥当性を管理する
* 期日(DueDate)パターン・・・約束の期日と判断を表現する
* 方針(Policy)パターン・・・様々なルールが複合する、複雑な業務ロジックを表現する
* 状態(State)パターン・・・状態と、状態遷移のできる/できないを表現する

#### 口座パターン

銀⾏の⼝座、在庫数量の管理、会計などで使うパターンです。
以下のしくみで実現します。
* 関⼼の対象を「⼝座」として⽤意する
* 数値の増減の「予定」を記録する
* 数値の増減の「実績」を記録する
* 現在の⼝座の「残⾼」を算出する

#### 期日パターン

* 約束を実⾏すべき期限を設定する
* その期限までに約束が適切に実⾏されることを監視する
* 期限切れの危険性について事前に通知する
* 期限までに実⾏されなかったことを検知する
* 期限切れの程度を判断する

DueDateクラスは、期⽇について汎⽤的に使いまわす部品ではありません。
出荷期⽇と⽀払期⽇という業務ルールがあった場合には、それぞれ異なる理由により、
異なる約束事が存在します。当然、約束が破られたときのルールも別々になります。

#### 方針(Policy)パターン

複合したルールを扱うためのひとつの⽅法として、
ルールの集合を持ったコレクションオブジェクトを作る方法がある。

### ドメインオブジェクトの設計を段階的に改善する

### 組み合わせて確認しながら改良する

ドメインモデルを作成していく中で改善するポイントは下記３つ。

* クラス名やメソッド名の変更
* ロジックの移動
* 取りまとめ役のクラスの導⼊

### 業務の言葉をコードと一致させると変更が楽になる

コードに登場する名前やプログラムの構造が業務の関⼼事と直接的に対応しているほど、
ソフトウェアの変更は楽で安全になります。

* クラス名が問題領域の関⼼事の⽤語と⼀致している
* メソッド名が利⽤者が知りたいこと／やってほしいことと⼀致している

### 業務を学びながらドメインモデルを成長させていく

ソースコードで業務の要求仕様を表現することをプログラムの⾃⼰⽂書化と呼びます。
要件を理解するために分析中に発⾒した⽤語は、そのままクラスの候補です。
関連するクラスはパッケージとしてグループ化して名前をつけてみます。
パッケージ名も、業務の関⼼事の表現⼿段です。業務を理解しながら、
クラスやパッケージの候補を⾒つけたら、実際にコードで書いてみます。

**オブジェクト指向では分析と設計は⼀体となった活動です。業務の知識がほとんどない初
期の段階でも、理解を確認するためにかんたんなコードを書いてみま
す。**

### 業務の理解がドメインモデルを洗練させる

ドメインモデルを設計するための基本は以下2点です。

* 重要な⾔葉とそうでない⾔葉を判断する
* ⾔葉と⾔葉の関係性を⾒つける

具体的にどうすれば良いかは、以下となります。
### 業務知識の暗黙知を引き出す
### 言葉をキャッチする
### 重要な言葉を見極めながらそれをドメインモデルに反映していく
⾔葉を正しく理解し、業務の⽂脈で意味が通じるように⾔葉を組み合わせて語れるようになれば、
ドメインモデルの⾻格はできたも同然です。
### 形式的な資料はかえって危険
重要な⾔葉が何で、⾻格となる関係は何かを判断することに時間と
エネルギーを使ったほうが、⼤きな成果を⼿に⼊れることができます。

全体を俯瞰しながら要点と重要な関係を共通理解にするためには、たとえば、次のような図法が役に⽴ちます。
・コンテキスト図・・・システムの目的を表す言葉を探す(重要なクラスの発見の手がかり)
・業務フロー図・・・コトの発生を時系列に整理する
・パッケージ図・・・業務の関心事を俯瞰する
・主要クラス図・・・重要な関心事とその関係を明確にする

### 言葉の曖昧さを具体的にする

### 基本語彙を増やす努力
ドメインモデルの設計のやり⽅として、
まず対象業務の基本知識を⾝に着けるところから始めなければいけません。
具体的には、次のような活動です。

* その業務のマニュアルや利⽤者ガイドを読んでみる
* その業務の⼀般的な知識を書籍などで勉強する
* その業務で使っているデータに何があるか画⾯やファイルを調べる
* その業務の経験者と会話する

### 繰り返しながら次第に知識を広げていく

### 改善を続けながらドメインモデルを成長させる


ドメインモデルの設計とはより良い解答を探し続けることです。
関係者間で設計レビューを行うと良いです。
チェックの⽅法は、基本的に「⾔葉」の使い⽅のチェックです。
業務の流れ（ユースケース）を、そのドメインモデルの内容で
実現できているかを確認します。

## CHAPTER 5 アプリケーション機能を組み立てる

三層＋ドメインモデルにおけるアプリケーション層のクラスの役割

* プレゼンテーション層からの依頼を受ける
* 適切なドメインオブジェクトに判断／加⼯／計算を依頼する
* プレゼンテーション層に結果（ドメインオブジェクト）を返す
* データソース層に記録や通知の⼊出⼒を指⽰する

### サービスクラスの設計はごちゃごちゃしやすい

* 業務ロジックは、サービスクラスに書かずにドメインオブジェクト
に任せる（サービスクラスで判断／加⼯／計算しない）
* 画⾯の複雑さをそのままサービスクラスに持ち込まない
* データベースの⼊出⼒の都合からサービスクラスを独⽴させる

### ドメインモデルを育てる

業務ロジックを書く適切なドメインオブジェクトがないのであれば、
あらたな業務知識の置き場所として、ドメインモデルにクラスを追加し
ます。業務ロジックの置き場所が明確になり、ほかのサービスクラスとの同じロジックの重複を防げるからです。

サービスクラスに業務ロジックを書きたくなったら、それはドメイン
モデルの改良の機会として積極的に活⽤しましょう。

### 小さく分ける

オブジェクト指向設計の基本は⼩さく分けて独⽴させた部品を⽤意す
ることです。対象が複雑なときは⼩さな単位に分けて、そのあとでそれ
らを組み合わせて⽬的を実現します。
サービスクラスの設計も、まずサービスを独⽴性の⾼い部品に分ける
ことを考えます。

```java

@Service
class BankAccountService {
  @Autowired
  BankAccountRepository repository;
  
  Amount balance() {
    return repository.balance();
  }
  boolean canWithdraw(Amount amount) {
    Amount balance = balance();
    return balance.has(amount);
  }
}

```

```java
@Service
class BankAccountUpdateService {
  @Autowired
  BankAccountRepository repository;
  void withdraw(Amount amount) {
    repository.withdraw(amount);
  }
}
```

意味のある最⼩単位で、かつ単独でテスト可能な単位にメソッドを分
割するのがサービスクラス設計の基本です。

### 小さく分けたサービスを組み立てる

⼩さく分けたメソッドを組み合わせて、実際に預⾦を引き出す機能は、
次のように3つのメソッドを組み合わせればよいわけです。

### 利用する側と提供する側の合意を明確にする

サービスを利⽤する側と、サービスを提供する側とで、サービス提供の約束ごとを決め、
設計をシンプルに保つ技法を**契約による設計**と呼びます。

契約による設計と対象的な技法が防御的プログラミングです。防御的
プログラミングでは、「サービスを提供する側は、利⽤する側が何をし
てくるかわからない」という前提でさまざまな防御的なロジックを書き
ます。利⽤する側も、提供側が何を返してくるかわからないという前提
で、戻ってきた値のnullチェックや、さまざまな検証のコードを書きま
す。

サービスクラスの設計にあたっては、プレゼンテーション層（使う
側）と、どういう約束事でサービスを提供するかを決めるのが設計が重
要なテーマです。
基本的な約束事には次のものがあります。
* nullを渡さない／nullを返さない
* 状態に依存する場合、使う側が事前に確認する
* 約束を守ったうえでさらに異常が起きた場合、例外で通知する


### シナリオクラスの効果

基本的なサービスクラスを組み合わせた複合サービスを提供するのが
シナリオクラスです。シナリオクラスは、コードの整理に役⽴つだけで
なく、次の2つの効果もあります。
* アプリケーション機能の説明
* シナリオテストの単位


### データベースの都合から分離する

repositoryでのメソッド宣言は、業務の関心ごととして宣言する。

```java
interface BankAccountRepository {
  boolean canWithdraw(Amount amount);
  Amount balance();
  void withDraw(Amount amount);
}
```

リポジトリは、ドメインオブジェクトの保管と取り出しができる架空の収納場所です。
テーブル設計に依存する⼼配ごとは、業務機能を記述するサービスクラスには不要です。そういうわずらわし
さを、リポジトリインターフェースの背後に隠すことで、アプリケーシ
ョン層のサービスクラスは業務の関⼼事だけに焦点を当てた、シンプル
な記述を保つことができます。


### CHAPTER 6 データベースの設計とドメインオブジェクト

### 状態の参照

* 基本はコトの記録のテーブル
* 導出の性能を考慮して、コトの記録のたびに状態を更新するテーブ
ルも⽤意する
* 状態を更新するテーブルはコトの記録からいつでも再構築可能な⼆
次的な導出データ

たとえば、⼝座に⼊⾦があったら⼊⾦テーブルにコトを記録する。そ
して、残⾼テーブルのその⼝座の残⾼も増やす。⼝座から出⾦があった
ら、出⾦テーブルにコトを記録する。そして残⾼テーブルのその⼝座の
残⾼を減らす。
データベースの本質は事実の記録です。まず、コトの記録を徹底する
ことが基本です。状態テーブルは補助的な役割であり、コトの記録から
派⽣させる⼆次的な情報です。


コトの記録と残⾼の更新を厳密なトランザクションとして処理するこ
とは、考え⽅として正しくありません。
コトの記録はデータの本質的な記録であり、残⾼の更新は⼆次的な導
出処理です。ですので、残⾼の更新に失敗したらコトの記録も取り消す
というのは、データの記録の考え⽅としてまちがっているのです。
もちろん、残⾼の更新が失敗したことを検知し、何らかの対応をとる
しくみは必要です。しかし、そのしくみは、本来のコトの記録からは独
⽴させるべきなのです。


コトの記録を徹底する理由は、
業務アプリケーションの中核の関心事が、「コト」の管理だからです。


コトの記録を基本にして、そこから派生する様々な情報を目的別に記録する方式を
**イベントソーシング**と言います。

また、コトの記録だけを記録し、状態は起きたコトの記録から動的に算出する方法もあります。
個人的には、データ量がそこまで多くならないものであれば動的に算出する、にとどめておいて、
将来的にその形での運用が難しくなった時に、状態を管理する2次テーブルを追加することを検討する形で良いのではと考えている。


第6章のまとめ
* 制約のないデータベースがプログラムを複雑にする
* 制約を徹底するとデータ管理がうまくいき、プログラムがわかりや
すくなる
* テーブル設計の基本は3つの制約（NOT NULL制約、⼀意性制約、
外部キー制約）
* 良いテーブル設計のコツは「コトの記録」の徹底
* 状態の更新はコトの記録とは独⽴させる
* オブジェクトとテーブルは設計の動機ややり⽅が基本的に異なる
* オブジェクトとテーブルの設計を独⽴させやすいしくみを活⽤する


## CHAPTER 7 画面とドメインオブジェクトの設計を連動させる

次の⽅針で関⼼事を整理すれば、画⾯アプリケーションの複雑さを改
善し、わかりやすく変更が楽で安全にできます。 

* さまざまな表⽰項⽬やボタンを詰め込んだ何でもできる汎⽤画⾯で
はなく、⽤途ごとのシンプルな画⾯に分ける
* 画⾯まわりのロジックから業務のロジックを分離する

