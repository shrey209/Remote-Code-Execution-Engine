variable "aws_region" {
  description = "AWS region to deploy to"
  default     = "ap-south-1" 
}

variable "instance_type" {
  description = "EC2 instance type"
  default     = "t2.micro" 
}

variable "ami_id" {
  description = "AMI ID for EC2 instance"
  default     = "ami-0e1b8bd9daef6633a" 
}
