provider "aws" {
  region = "eu-central-1"
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnet_ids" "default" {
  vpc_id = data.aws_vpc.default.id
}

variable "database_name" {
  type        = string
  default     = "apidatabase"
}

data "aws_ami" "image" {
  most_recent = true
  owners = ["self"]
  filter {                       
    name = "tag:Application"     
    values = ["recipes-api"]
  }                              
}

// DATABASE
resource "aws_db_instance" "recipes_api_db" {
  engine                  = "postgres"
  instance_class          = "db.t2.micro"
  name                    = var.database_name
  username                = "postgres"
  password                = "recipespostgrespass"
  skip_final_snapshot     = true
  allocated_storage       = 20
  vpc_security_group_ids  = [aws_security_group.recipes_db_sg.id]
}

// SECURITY GROUPS
resource "aws_security_group" "recipes_api_sg" {
  name = "recipes-api-sg"

  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "recipes_db_sg" {
  name = "recipes-db-sg"

  ingress {
    from_port = 5432
    to_port = 5432
    protocol = "tcp"
    security_groups = [aws_security_group.recipes_api_sg.id]
  }
}

// LAUNCH CONFIGURATION & AUTOSCALE
resource "aws_launch_configuration" "recipes_api" {
  image_id = data.aws_ami.image.id
  instance_type = "t2.micro"
  security_groups = [aws_security_group.recipes_api_sg.id]
  user_data = <<-EOF
    #! /bin/bash
    sed -i 's,DATABASE_URL_TOKEN,jdbc:postgresql://${aws_db_instance.recipes_api_db.endpoint}/${var.database_name},g' /opt/recipes-api/conf/prod.conf
    EOF

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_autoscaling_group" "recipes_api_asg" {
  launch_configuration = aws_launch_configuration.recipes_api.name
  vpc_zone_identifier = data.aws_subnet_ids.default.ids

  target_group_arns = [aws_lb_target_group.recipes_api_tg.arn]
  health_check_type = "ELB"

  min_size = 2
  max_size = 2

  tag {
    key = "Name"
    value = "recipes-api-asg"
    propagate_at_launch = true
  }
}

// LOAD BALANCER
resource "aws_lb" "recipes_api_lb" {
  name = "recipes-api"
  load_balancer_type = "application"
  subnets = data.aws_subnet_ids.default.ids
  security_groups = [aws_security_group.recipes_api_sg.id]
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.recipes_api_lb.arn
  port = 80
  protocol = "HTTP"

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "Page Not Found"
      status_code = 404
    }
  }
}

resource "aws_lb_target_group" "recipes_api_tg" {
  name = "recipes-api-tg"
  port = 80
  protocol = "HTTP"
  vpc_id = data.aws_vpc.default.id

  health_check {
    path = "/"
    protocol = "HTTP"
    matcher = "200"
    interval = 15
    timeout = 5
    healthy_threshold = 3
    unhealthy_threshold = 3
  }
}

resource "aws_lb_listener_rule" "asg" {
  listener_arn = aws_lb_listener.http.arn
  priority = 100

  condition {
    path_pattern {
      values = ["*"]
    }
  }

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.recipes_api_tg.arn
  }
}

output "lb_dns_name" {
  value = "http://${aws_lb.recipes_api_lb.dns_name}"
  description = "Domain name of the Recipes api application load balancer"
}

output "ami_id" {
  value = data.aws_ami.image.id
}
