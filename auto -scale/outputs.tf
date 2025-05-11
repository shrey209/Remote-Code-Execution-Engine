output "load_balancer_dns" {
  description = "Public DNS name of the Load Balancer"
  value       = aws_lb.alb.dns_name
}

output "asg_name" {
  description = "Auto Scaling Group Name"
  value       = aws_autoscaling_group.asg.name
}

output "launch_template_id" {
  description = "Launch template ID"
  value       = aws_launch_template.lt.id
}

output "alb_target_group_arn" {
  value = aws_lb_target_group.tg.arn
}
