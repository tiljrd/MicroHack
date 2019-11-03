var canvas = new Object();
canvas.element = document.getElementById('canvas');
canvas.context = canvas.element.getContext('2d');
canvas.width = canvas.element.getAttribute('width');
canvas.height = canvas.element.getAttribute('height');
canvas.context.font = "30px Arial";
canvas.cellWidth = 20;

var cw = 20;
var maxx = canvas.width/cw;
var maxy = canvas.height/cw;

var food;
var score;
var player2 = false;

window.onload=init;

	function init()
	{
		document.getElementById("Game_Over").innerHTML = "";
	    var now = new Date().getTime();
        var millisecondsToWait = 1000;
        while ( new Date().getTime() < now + millisecondsToWait ){
        }
		let snake = new Snake();
		let snake2 = new Snake();
		
		create_food();
		
		document.onkeydown = function() {keystroke(event, snake, 1)};
		if (player2) {
		    document.onkeydown = function() {keystroke(event, snake2, 2)};
		}
	
		score = 0;
		
		if(typeof game_loop != "undefined") clearInterval(game_loop);
		if (player2) {
		    game_loop = setInterval(function() {paint(snake, snake2)}, 80);
		}
		else {
		    game_loop = setInterval(function() {paint(snake, snake)}, 80);
		}
		document.getElementById("Retry").innerHTML = '<button style="width:200px;height:120px;font-size:40px;" onclick=player2Toggle()>Player 2</button>';
	}
	
	function paint(snake, snake2) {
		if (!(snake.check_collision() || snake2.check_collision())) {
			canvas.context.fillStyle = "white";
			canvas.context.fillRect(0, 0, canvas.width, canvas.height);
			canvas.context.strokeStyle = "black";
			canvas.context.strokeRect(0, 0, canvas.width, canvas.height);
			
			isEating(snake);
			snake.update_snake();
			
			if (player2) {
			    isEating(snake2);
			    snake2.update_snake();
			}
			
			for(var i = 0; i < snake.getPositions().length; i++) {
				var c = snake.getPositions()[i];
				paint_cell(c.x, c.y);
			}
			
			if (player2) {
			    for(var i = 0; i < snake2.getPositions().length; i++) {
				    var c = snake2.getPositions()[i];
				    paint_cell(c.x, c.y);
			    }
			}
			
			paint_cell(food.x, food.y);
			
			snake.setChangedDirection();
		}
		
		else {
			clearInterval(game_loop);
			canvas.context.fillStyle = "white";
			canvas.context.fillRect(0, 0, canvas.width, canvas.height);
			canvas.context.strokeStyle = "black";
			canvas.context.strokeRect(0, 0, canvas.width, canvas.height);
			document.getElementById("Retry").innerHTML = '<button style="width:200px;height:120px;font-size:40px;" onclick="init()">Retry</button>';
			document.getElementById("Game_Over").innerHTML = "Game over&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Score: " + score;
		}
	}
	
	function paint_cell(x, y) {
		var r = Math.floor((Math.random() * 254) + 1);
		var g = Math.floor((Math.random() * 254) + 1);
		var b = Math.floor((Math.random() * 254) + 1);
		canvas.context.fillStyle = ("rgb("+r+","+g+","+b+")");
		canvas.context.fillRect(x*cw, y*cw, cw, cw);
		canvas.context.fillStyle = ("black");
		canvas.context.strokeStyle = "white";
		canvas.context.strokeRect(x*cw, y*cw, cw, cw);
	}
	
	function isEating(snake) {
		if (snake.positions[0].x==food.x && snake.positions[0].y==food.y){
			create_food();
			snake.eat();
			score++;
		}
	}
	
	function player2Toggle() {
	    if (player2) {
	        player2=false;
	    }
	    else {
	        player2=true;
	    }
	}
